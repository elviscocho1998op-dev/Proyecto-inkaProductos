package pe.cibertec.inkaproductos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.cibertec.inkaproductos.models.MensajeTi;
import java.util.List;

@Repository
public interface MensajeTiRepository extends JpaRepository<MensajeTi, Integer> {

    // TI ordena por prioridad para saber qué atender primero
    List<MensajeTi> findByEstadoOrderByPrioridadDesc(String estado);

    List<MensajeTi> findByEmisorOrderByFechaEnvioDesc(String email);
}