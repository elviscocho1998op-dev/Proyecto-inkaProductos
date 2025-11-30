package pe.cibertec.inkaproductos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.cibertec.inkaproductos.model.Producto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> { // Cambiado Long a Integer para coincidir con la Entidad

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Producto> buscarPorNombre(@Param("texto") String texto);
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    Optional<Producto> findBySku(String sku);

    List<Producto> findByCategoriaCategoriaId(Integer categoriaId);

    List<Producto> findByActivoTrue();
}