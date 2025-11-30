package pe.cibertec.inkaproductos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.cibertec.inkaproductos.model.Unidad;
import pe.cibertec.inkaproductos.repository.UnidadRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UnidadService {

    @Autowired
    private UnidadRepository repo;

    public List<Unidad> listar() {
        return repo.findAll();
    }

    public Optional<Unidad> obtenerPorId(Integer id) {
        return repo.findById(id);
    }

    public Unidad guardar(Unidad unidad) {
        return repo.save(unidad);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}