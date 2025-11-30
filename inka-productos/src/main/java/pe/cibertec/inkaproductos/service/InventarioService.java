package pe.cibertec.inkaproductos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.inkaproductos.model.Inventario;
import pe.cibertec.inkaproductos.model.Inventario.InventarioId;
import pe.cibertec.inkaproductos.repository.InventarioRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository repo;

    @Autowired
    private AlmacenService almacenService;

    @Autowired
    private ProductoService productoService;

    // lista filtros
    public List<Inventario> listar(Integer almacenId, Integer categoriaId) {
        if (almacenId != null && categoriaId != null) {
            return repo.findByAlmacenAlmacenIdAndProductoCategoriaCategoriaId(almacenId, categoriaId);
        } else if (almacenId != null) {
            return repo.findByAlmacenAlmacenId(almacenId);
        } else if (categoriaId != null) {
            return repo.findByProductoCategoriaCategoriaId(categoriaId);
        }
        return repo.findAll();
    }

    // obtiene el id
    public Optional<Inventario> obtenerPorId(InventarioId id) {
        return repo.findById(id);
    }

    // aqui actualiza stock
    @Transactional
    public void actualizarStock(Integer almacenId, Integer productoId, BigDecimal cantidadCambio) {
        InventarioId id = new InventarioId(almacenId, productoId);

        Inventario inventario = repo.findById(id).orElseGet(() -> {
            Inventario nuevo = new Inventario();
            nuevo.setId(id);
            nuevo.setCantidad(BigDecimal.ZERO);
            nuevo.setStockMin(BigDecimal.ZERO);

            nuevo.setAlmacen(almacenService.obtenerPorId(almacenId).orElse(null));
            nuevo.setProducto(productoService.obtenerPorId(productoId).orElse(null));

            return nuevo;
        });

        BigDecimal nuevaCantidad = inventario.getCantidad().add(cantidadCambio);

        if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Stock insuficiente. Operación cancelada.");
        }

        inventario.setCantidad(nuevaCantidad);
        repo.save(inventario);
    }
}