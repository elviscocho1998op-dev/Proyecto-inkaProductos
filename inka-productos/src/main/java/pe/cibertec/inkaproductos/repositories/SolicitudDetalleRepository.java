package pe.cibertec.inkaproductos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.inkaproductos.models.SolicitudCompraDetalle;

import java.util.List;

public interface SolicitudDetalleRepository extends JpaRepository<SolicitudCompraDetalle, Integer> {

}
