package pe.cibertec.inkaproductos.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.inkaproductos.models.*;
import pe.cibertec.inkaproductos.repositories.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudCompraService {

    private final SolicitudCompraRepository solicitudRepository;
    private final InventarioRepository inventarioRepository;

    @Transactional
    public SolicitudCompra procesarCompra(SolicitudCompra solicitud, String rolUsuario) {
        if ("ADMIN".equals(rolUsuario)) {
            // El ADMIN procesa de inmediato: Resta del origen y suma al destino
            solicitud.setEstado("PROCESADA");
            actualizarStockMasivo(solicitud);
        } else {
            // El USER solo deja la solicitud pendiente para aprobación
            solicitud.setEstado("PENDIENTE");
        }
        return solicitudRepository.save(solicitud);
    }

    private void actualizarStockMasivo(SolicitudCompra solicitud) {
        for (SolicitudCompraDetalle detalle : solicitud.getDetalles()) {
            // 1. Descontar del almacén origen
            actualizarStock(solicitud.getOrigen().getAlmacenId(),
                    detalle.getProducto().getProductoId(), -detalle.getCantidad());

            // 2. Aumentar en el almacén destino
            actualizarStock(solicitud.getDestino().getAlmacenId(),
                    detalle.getProducto().getProductoId(), detalle.getCantidad());
        }
    }

    private void actualizarStock(Integer almacenId, Integer productoId, Double cantidad) {
        Inventario inv = inventarioRepository.buscarProductoEnAlmacen(almacenId, productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en almacén " + almacenId));
        inv.setCantidad(inv.getCantidad() + cantidad);
        inventarioRepository.save(inv);
    }
}