package pe.cibertec.inkaproductos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.cibertec.inkaproductos.model.Inventario;
import pe.cibertec.inkaproductos.model.Inventario.InventarioId;

import java.util.List;


@Repository
public interface InventarioRepository extends JpaRepository<Inventario, InventarioId> {


    List<Inventario> findByAlmacenAlmacenIdAndProductoCategoriaCategoriaId(Integer almacenId, Integer categoriaId);


    List<Inventario> findByProductoCategoriaCategoriaId(Integer categoriaId);
    List<Inventario> findByAlmacenAlmacenId(Integer almacenId);
}