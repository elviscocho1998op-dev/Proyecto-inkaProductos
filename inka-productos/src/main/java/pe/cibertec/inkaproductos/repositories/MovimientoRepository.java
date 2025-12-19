package pe.cibertec.inkaproductos.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import pe.cibertec.inkaproductos.models.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
}
