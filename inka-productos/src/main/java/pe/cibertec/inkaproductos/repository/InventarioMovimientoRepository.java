package pe.cibertec.inkaproductos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.cibertec.inkaproductos.model.InventarioMovimiento;

import java.util.List;

@Repository
public interface InventarioMovimientoRepository extends JpaRepository<InventarioMovimiento, Long> {

    List<InventarioMovimiento> findAllByOrderByFechaMovimientoDesc();

    List<InventarioMovimiento> findByAlmacenAlmacenIdOrderByFechaMovimientoDesc(Integer almacenId);
}