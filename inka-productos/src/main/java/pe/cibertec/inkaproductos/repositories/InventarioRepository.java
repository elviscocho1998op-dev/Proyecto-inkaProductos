package pe.cibertec.inkaproductos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.cibertec.inkaproductos.models.Inventario;
import pe.cibertec.inkaproductos.models.InventarioId;
import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, InventarioId> {

    // Lista todo el stock de un almacén específico
    @Query("SELECT i FROM Inventario i WHERE i.almacen.almacenId = :idAlmacen")
    List<Inventario> listarPorAlmacen(@Param("idAlmacen") Integer idAlmacen);

    // Busca un producto específico en un almacén específico
    @Query("SELECT i FROM Inventario i WHERE i.almacen.almacenId = :idAlmacen AND i.producto.productoId = :idProducto")
    Optional<Inventario> buscarProductoEnAlmacen(
            @Param("idAlmacen") Integer idAlmacen,
            @Param("idProducto") Integer idProducto
    );
}