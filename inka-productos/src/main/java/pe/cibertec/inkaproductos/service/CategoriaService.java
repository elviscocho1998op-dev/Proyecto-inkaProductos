package pe.cibertec.inkaproductos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.cibertec.inkaproductos.model.Categoria;
import pe.cibertec.inkaproductos.repository.CategoriaRepository;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repo;

    public List<Categoria> listar() {
        return repo.findAll();
    }

    public Optional<Categoria> obtenerPorId(Integer id) {
        return repo.findById(id);
    }

    public Categoria guardar(Categoria categoria) {
        return repo.save(categoria);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}