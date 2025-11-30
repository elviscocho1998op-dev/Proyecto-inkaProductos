package pe.cibertec.inkaproductos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.cibertec.inkaproductos.model.ProductoUnidad;
import pe.cibertec.inkaproductos.model.ProductoUnidad.ProductoUnidadId;

import java.util.List;

@Repository
public interface ProductoUnidadRepository extends JpaRepository<ProductoUnidad, ProductoUnidadId> {

    List<ProductoUnidad> findByProductoProductoId(Integer productoId);
}