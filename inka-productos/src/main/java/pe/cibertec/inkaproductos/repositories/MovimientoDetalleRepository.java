package pe.cibertec.inkaproductos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.inkaproductos.models.MovimientoDetalle;

public interface MovimientoDetalleRepository extends JpaRepository<MovimientoDetalle, Long> {
}