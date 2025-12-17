package pe.cibertec.inkaproductos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.inkaproductos.models.SolicitudCompraDetalle;

import java.util.List;

public interface SolicitudDetalleRepository extends JpaRepository<SolicitudCompraDetalle, Integer> {

    List<SolicitudCompraDetalle> findBySolicitud_SolicitudId(Integer solicitudId);

    // Opcional: Buscar en qué compras se ha movido un producto específico
    List<SolicitudCompraDetalle> findByProducto_ProductoId(Integer productoId);
}
