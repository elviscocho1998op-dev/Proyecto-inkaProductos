package pe.cibertec.inkaproductos.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.inkaproductos.model.Producto;
import pe.cibertec.inkaproductos.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoRestController {

    @Autowired
    private ProductoService service;

    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return new ResponseEntity<>(service.guardar(producto), HttpStatus.CREATED);
    }
}