package pe.cibertec.inkaproductos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.cibertec.inkaproductos.model.Unidad;

import java.util.List;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Integer> {

    List<Unidad> findByActivoTrue();
}