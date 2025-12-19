package pe.cibertec.inkaproductos.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.inkaproductos.dto.TransaccionDTO;
import pe.cibertec.inkaproductos.models.*;
import pe.cibertec.inkaproductos.repositories.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudService {

    private final SolicitudCompraRepository repoSolicitud;
    private final SolicitudDetalleRepository repoDetalle;
    private final AlmacenRepository repoAlmacen;
    private final ProductoRepository repoProducto;

    // ⭐ NECESARIOS PARA APROBAR
    private final MovimientoRepository repoMovimiento;
    private final MovimientoDetalleRepository repoMovimientoDetalle;

    // ⭐ USAMOS AJUSTAR STOCK DEL PRODUCTO SERVICE
    private final ProductoService productoService;


    // ============================================================
    //                      CREAR SOLICITUD (USER)
    // ============================================================
    @Transactional
    public SolicitudCompra crearSolicitud(TransaccionDTO dto) {

        if (dto.getOrigenId() == null || dto.getDestinoId() == null)
            throw new RuntimeException("Origen y destino son obligatorios");

        if (dto.getOrigenId().equals(dto.getDestinoId()))
            throw new RuntimeException("El origen y destino deben ser distintos");

        if (dto.getItems() == null || dto.getItems().isEmpty())
            throw new RuntimeException("La solicitud está vacía");

        // CABECERA
        SolicitudCompra sc = new SolicitudCompra();
        sc.setOrigen(repoAlmacen.findById(dto.getOrigenId()).orElseThrow());
        sc.setDestino(repoAlmacen.findById(dto.getDestinoId()).orElseThrow());
        sc.setUsuarioSolicitante(dto.getUsuarioEmail());
        sc.setEstado("PENDIENTE");

        sc = repoSolicitud.save(sc);

        // DETALLES
        List<SolicitudCompraDetalle> detalles = new ArrayList<>();

        for (var item : dto.getItems()) {
            SolicitudCompraDetalle d = new SolicitudCompraDetalle();
            d.setSolicitud(sc);
            d.setProducto(repoProducto.findById(item.getProductoId()).orElseThrow());
            d.setCantidad(item.getCantidad());
            detalles.add(d);
        }

        repoDetalle.saveAll(detalles);

        return sc;
    }


    // ============================================================
    //                     LISTADOS
    // ============================================================
    public List<SolicitudCompra> listarPorUsuario(String email) {
        return repoSolicitud.findByUsuarioSolicitante(email);
    }

    public List<SolicitudCompra> listarPendientes() {
        return repoSolicitud.findByEstado("PENDIENTE");
    }


    // ============================================================
    //                      APROBAR SOLICITUD (ADMIN)
    // ============================================================
    @Transactional
    public Movimiento aprobarSolicitud(Integer solicitudId) {

        SolicitudCompra sol = repoSolicitud.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!sol.getEstado().equals("PENDIENTE"))
            throw new RuntimeException("La solicitud ya fue procesada");

        // 1) Cambiar estado de la solicitud
        sol.setEstado("APROBADA");
        repoSolicitud.save(sol);

        // 2) Crear movimiento asociado
        Movimiento mov = new Movimiento();
        mov.setUsuario(sol.getUsuarioSolicitante());
        mov.setOrigen(sol.getOrigen());
        mov.setDestino(sol.getDestino());
        mov.setEstado(Movimiento.EstadoMovimiento.APROBADO);

        mov = repoMovimiento.save(mov);

        // 3) Registrar detalles y mover stock real
        for (SolicitudCompraDetalle d : sol.getDetalles()) {

            // Registrar detalle del movimiento
            MovimientoDetalle md = new MovimientoDetalle();
            md.setMovimiento(mov);
            md.setProducto(d.getProducto());
            md.setCantidad(d.getCantidad());
            repoMovimientoDetalle.save(md);

            // ⭐ MOVER STOCK REAL (USAMOS TU MÉTODO ORIGINAL) ⭐
            productoService.moverStock(
                    sol.getOrigen().getAlmacenId(),
                    d.getProducto().getProductoId(),
                    -d.getCantidad()  // Descontar
            );

            productoService.moverStock(
                    sol.getDestino().getAlmacenId(),
                    d.getProducto().getProductoId(),
                    d.getCantidad()   // Sumar
            );
        }

        return mov;
    }

}
