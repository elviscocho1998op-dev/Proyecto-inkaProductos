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
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final SolicitudCompraRepository solicitudRepo;
    private final SolicitudDetalleRepository detalleRepo;
    private final InventarioRepository inventarioRepo;

    // ⭐ NUEVO
    private final MovimientoRepository movimientoRepo;
    private final MovimientoDetalleRepository movimientoDetalleRepo;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public List<SolicitudCompra> listarHistorial() {
        return solicitudRepo.findAll();
    }

    @Transactional
    public void procesarTransaccion(TransaccionDTO datos) {

        Integer origenId  = datos.getOrigenId();
        Integer destinoId = datos.getDestinoId();

        if (origenId == null || destinoId == null)
            throw new RuntimeException("Origen y destino son obligatorios");

        if (origenId.equals(destinoId))
            throw new RuntimeException("Origen y destino no pueden ser iguales");

        if (datos.getItems() == null || datos.getItems().isEmpty())
            throw new RuntimeException("No hay items para procesar");

        boolean esAprobadaDirecta = datos.isEsAdmin();

        // =====================================================
        // 1) GUARDAR CABECERA DE SOLICITUD
        // =====================================================
        SolicitudCompra cabecera = new SolicitudCompra();

        Almacen org = new Almacen();
        org.setAlmacenId(origenId);

        Almacen dest = new Almacen();
        dest.setAlmacenId(destinoId);

        cabecera.setOrigen(org);
        cabecera.setDestino(dest);
        cabecera.setUsuarioSolicitante(
                datos.getUsuarioEmail() != null ? datos.getUsuarioEmail() : "Sistema"
        );
        cabecera.setEstado(esAprobadaDirecta ? "APROBADA" : "PENDIENTE");

        cabecera = solicitudRepo.save(cabecera);

        // =====================================================
        // 2) DETALLES + MOVIMIENTO DE STOCK (si ADMIN)
        // =====================================================
        List<SolicitudCompraDetalle> detalles = new ArrayList<>();

        for (var item : datos.getItems()) {

            if (item.getProductoId() == null)
                throw new RuntimeException("ProductoId es obligatorio");

            double cant = item.getCantidad().doubleValue();

            SolicitudCompraDetalle det = new SolicitudCompraDetalle();
            det.setSolicitud(cabecera);

            Producto p = new Producto();
            p.setProductoId(item.getProductoId());
            det.setProducto(p);

            det.setCantidad(cant);
            detalles.add(det);

            if (esAprobadaDirecta) {
                ajustarStock(origenId, item.getProductoId(), -cant);
                ajustarStock(destinoId, item.getProductoId(), cant);
            }
        }

        detalleRepo.saveAll(detalles);

        // =====================================================
        // ⭐⭐⭐ 3) GUARDAR MOVIMIENTO EN HISTORIAL ⭐⭐⭐
        // =====================================================
        if (esAprobadaDirecta) {

            Movimiento mov = new Movimiento();

            mov.setUsuario(datos.getUsuarioEmail());

            Almacen a1 = new Almacen();
            a1.setAlmacenId(origenId);
            mov.setOrigen(a1);

            Almacen a2 = new Almacen();
            a2.setAlmacenId(destinoId);
            mov.setDestino(a2);

            mov.setEstado(Movimiento.EstadoMovimiento.APROBADO);

            mov = movimientoRepo.save(mov);

            // DETALLES DEL MOVIMIENTO
            List<MovimientoDetalle> mds = new ArrayList<>();

            for (var item : datos.getItems()) {

                MovimientoDetalle md = new MovimientoDetalle();
                md.setMovimiento(mov);

                Producto prod = new Producto();
                prod.setProductoId(item.getProductoId());
                md.setProducto(prod);

                md.setCantidad(item.getCantidad().doubleValue());

                mds.add(md);
            }

            movimientoDetalleRepo.saveAll(mds);
        }
    }

    private void ajustarStock(Integer almacenId, Integer productoId, Double delta) {

        Inventario inv = inventarioRepo.buscarProductoEnAlmacen(almacenId, productoId)
                .orElseGet(() -> {
                    Inventario nuevo = new Inventario();

                    InventarioId id = new InventarioId();
                    id.setAlmacenId(almacenId);
                    id.setProductoId(productoId);

                    Almacen a = new Almacen();
                    a.setAlmacenId(almacenId);

                    Producto p = new Producto();
                    p.setProductoId(productoId);

                    nuevo.setId(id);
                    nuevo.setAlmacen(a);
                    nuevo.setProducto(p);
                    nuevo.setCantidad(0.0);

                    return inventarioRepo.save(nuevo);
                });

        double nuevaCantidad = inv.getCantidad() + delta;
        if (nuevaCantidad < 0)
            throw new RuntimeException("Stock insuficiente en almacén " + almacenId);

        inv.setCantidad(nuevaCantidad);
        inventarioRepo.save(inv);
    }

    // ===================================================
// CRUD de productos (para que el Controller no falle)
// ===================================================
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> filtrar(Integer categoriaId, Integer almacenId) {
        Integer catId = (categoriaId != null && categoriaId > 0) ? categoriaId : null;
        Integer almId = (almacenId != null && almacenId > 0) ? almacenId : null;

        return productoRepository.filtrarProductos(catId, almId);
    }


    public void moverStock(Integer almacenId, Integer productoId, Double cantidad) {
        ajustarStock(almacenId, productoId, cantidad);
    }

}
