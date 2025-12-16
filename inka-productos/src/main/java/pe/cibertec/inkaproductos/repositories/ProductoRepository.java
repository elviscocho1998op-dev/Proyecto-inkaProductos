package pe.cibertec.inkaproductos.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.cibertec.inkaproductos.models.Producto;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Producto> buscarPorNombreOSku(@Param("texto") String texto);

    // Paginación para el listado general en Angular
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Listar productos activos
    List<Producto> findByActivo(Integer activo);
}