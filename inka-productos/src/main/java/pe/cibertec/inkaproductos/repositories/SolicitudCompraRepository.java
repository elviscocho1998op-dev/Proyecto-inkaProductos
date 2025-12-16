package pe.cibertec.inkaproductos.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.inkaproductos.models.SolicitudCompra;
import java.util.List;

public interface SolicitudCompraRepository extends JpaRepository<SolicitudCompra, Integer> {

    // Listar compras por estado (PENDIENTE, APROBADA, etc.)
    List<SolicitudCompra> findByEstadoIgnoreCase(String estado);

    // Listar compras solicitadas por un usuario específico (para trazabilidad)
    Page<SolicitudCompra> findByUsuarioSolicitante(String usuario, Pageable pageable);
}