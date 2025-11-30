package pe.cibertec.inkaproductos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.cibertec.inkaproductos.model.Producto;
import pe.cibertec.inkaproductos.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repo;

    public List<Producto> listar() {
        return repo.findAll();
    }

    public List<Producto> listarActivos() {
        return repo.findByActivoTrue();
    }

    public Optional<Producto> obtenerPorId(Integer id) {
        return repo.findById(id);
    }

    public Producto guardar(Producto producto) {
        return repo.save(producto);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}