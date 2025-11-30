package pe.cibertec.inkaproductos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.cibertec.inkaproductos.model.SolicitudCompra;
import pe.cibertec.inkaproductos.model.SolicitudCompra.EstadoSolicitud;

import java.util.List;

@Repository
public interface SolicitudCompraRepository extends JpaRepository<SolicitudCompra, Long> {

    List<SolicitudCompra> findByEstado(EstadoSolicitud estado);
    List<SolicitudCompra> findByUsuarioSolicitanteOrderByFechaSolicitudDesc(String usuarioSolicitante);
}