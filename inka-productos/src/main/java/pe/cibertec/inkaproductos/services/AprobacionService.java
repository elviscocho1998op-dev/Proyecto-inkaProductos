package pe.cibertec.inkaproductos.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.inkaproductos.models.*;
import pe.cibertec.inkaproductos.repositories.InventarioRepository;
import pe.cibertec.inkaproductos.repositories.SolicitudCompraRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AprobacionService {

    private final SolicitudCompraRepository solicitudRepo;
    private final InventarioRepository inventarioRepository;

    public List<SolicitudCompra> listarPendientes() {
        return solicitudRepo.findByEstadoIgnoreCase("PENDIENTE");
    }

    @Transactional
    public SolicitudCompra aprobar(Integer solicitudId) {
        SolicitudCompra sc = solicitudRepo.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + solicitudId));

        if (!"PENDIENTE".equalsIgnoreCase(sc.getEstado())) {
            throw new RuntimeException("Solo se puede aprobar una solicitud PENDIENTE");
        }

        // Mover stock SOLO al aprobar
        for (SolicitudCompraDetalle d : sc.getDetalles()) {
            Integer origenId  = sc.getOrigen().getAlmacenId();
            Integer destinoId = sc.getDestino().getAlmacenId();
            Integer prodId    = d.getProducto().getProductoId();
            double cant       = d.getCantidad();

            // descontar origen
            ajustarStock(origenId, prodId, -cant);

            // sumar destino
            ajustarStock(destinoId, prodId, cant);
        }

        sc.setEstado("APROBADA");
        return solicitudRepo.save(sc);
    }

    @Transactional
    public SolicitudCompra rechazar(Integer solicitudId) {
        SolicitudCompra sc = solicitudRepo.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + solicitudId));

        if (!"PENDIENTE".equalsIgnoreCase(sc.getEstado())) {
            throw new RuntimeException("Solo se puede rechazar una solicitud PENDIENTE");
        }

        sc.setEstado("RECHAZADA");
        return solicitudRepo.save(sc);
    }

    private void ajustarStock(Integer almacenId, Integer productoId, Double delta) {

        Inventario inv = inventarioRepository.buscarProductoEnAlmacen(almacenId, productoId)
                .orElseGet(() -> {
                    // crear fila inventario si no existe
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
                    return inventarioRepository.save(nuevo);
                });

        double nuevaCantidad = inv.getCantidad() + delta;
        if (nuevaCantidad < 0) {
            throw new RuntimeException("Stock insuficiente en almacén " + almacenId + " para producto " + productoId);
        }

        inv.setCantidad(nuevaCantidad);
        inventarioRepository.save(inv);
    }

}
