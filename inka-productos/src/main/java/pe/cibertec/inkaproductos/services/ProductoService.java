package pe.cibertec.inkaproductos.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.cibertec.inkaproductos.models.*;
import pe.cibertec.inkaproductos.repositories.ProductoRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> filtrar(Integer categoriaId, Integer almacenId) {

        Integer catId = (categoriaId != null && categoriaId > 0) ? categoriaId : null;
        Integer almId = (almacenId != null && almacenId > 0) ? almacenId : null;

        return productoRepository.filtrarProductos(categoriaId, almacenId);
    }


}