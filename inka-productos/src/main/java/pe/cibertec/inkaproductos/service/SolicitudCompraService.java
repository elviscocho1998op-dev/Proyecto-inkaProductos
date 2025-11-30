package pe.cibertec.inkaproductos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.inkaproductos.model.*;
import pe.cibertec.inkaproductos.repository.SolicitudCompraRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SolicitudCompraService {

    @Autowired
    private SolicitudCompraRepository repo;

    @Autowired
    private InventarioMovimientoService movimientoService; // Para mover stock real

    // Listar todo (Para Admin)
    public List<SolicitudCompra> listarTodas() {
        return repo.findAll();
    }

    // Listar solo pendientes (Para bandeja de entrada del Admin)
    public List<SolicitudCompra> listarPendientes() {
        return repo.findByEstado(SolicitudCompra.EstadoSolicitud.PENDIENTE);
    }

    public List<SolicitudCompra> listarPorUsuario(String email) {
        return repo.findByUsuarioSolicitanteOrderByFechaSolicitudDesc(email);
    }


    @Transactional
    public void crearSolicitud(SolicitudCompra solicitud, boolean isAdmin) {
        if (solicitud.getCantidad() == null || solicitud.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        if (isAdmin) {
            solicitud.setEstado(SolicitudCompra.EstadoSolicitud.APROBADO);
            repo.save(solicitud);
            generarMovimientoEntrada(solicitud, "COMPRA_DIRECTA_ADMIN");
        } else {
            solicitud.setEstado(SolicitudCompra.EstadoSolicitud.PENDIENTE);
            repo.save(solicitud);
        }
    }
  //LOGICA PARA EL ADMIN
    @Transactional
    public void aprobarSolicitud(Long id, String usuarioAdmin) {
        SolicitudCompra solicitud = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (solicitud.getEstado() != SolicitudCompra.EstadoSolicitud.PENDIENTE) {
            throw new RuntimeException("Esta solicitud ya fue procesada.");
        }

        solicitud.setEstado(SolicitudCompra.EstadoSolicitud.APROBADO);
        repo.save(solicitud);

        generarMovimientoEntrada(solicitud, "APROBADO_POR_" + usuarioAdmin);
    }

    @Transactional
    public void rechazarSolicitud(Long id) {
        SolicitudCompra solicitud = repo.findById(id).orElseThrow();
        solicitud.setEstado(SolicitudCompra.EstadoSolicitud.RECHAZADO);
        repo.save(solicitud);
    }

    private void generarMovimientoEntrada(SolicitudCompra sol, String referencia) {
        InventarioMovimiento mov = new InventarioMovimiento();
        mov.setAlmacen(sol.getAlmacen());
        mov.setProducto(sol.getProducto());


        mov.setTipoMovimiento(InventarioMovimiento.TipoMovimiento.ENTRADA);
        mov.setCantidad(sol.getCantidad());
        mov.setReferencia(referencia);
        mov.setUsuario(sol.getUsuarioSolicitante());

        movimientoService.guardar(mov);
    }
}