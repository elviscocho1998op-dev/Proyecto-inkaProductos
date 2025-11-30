package pe.cibertec.inkaproductos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.cibertec.inkaproductos.model.Almacen;
import pe.cibertec.inkaproductos.repository.AlmacenRepository;
import java.util.List;
import java.util.Optional;

@Service
public class AlmacenService {

    @Autowired
    private AlmacenRepository repo;

    public List<Almacen> listar() {
        return repo.findAll();
    }

    public Optional<Almacen> obtenerPorId(Integer id) {
        return repo.findById(id);
    }

    public Almacen guardar(Almacen almacen) {
        return repo.save(almacen);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}