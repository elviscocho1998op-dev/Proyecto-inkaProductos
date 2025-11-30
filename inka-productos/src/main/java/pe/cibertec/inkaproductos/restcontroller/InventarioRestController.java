package pe.cibertec.inkaproductos.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

// CRUCIAL: Importamos la Entidad completa para poder usar la llave compuesta InventarioId
import pe.cibertec.inkaproductos.model.Inventario;
import pe.cibertec.inkaproductos.service.InventarioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
public class InventarioRestController {

    @Autowired
    private InventarioService inventarioService;


    @GetMapping
    public List<Inventario> listar(
            @RequestParam(required = false) Integer almacenId,   // Nuevo
            @RequestParam(required = false) Integer categoriaId  // Existente
    ) {

        return inventarioService.listar(almacenId, categoriaId);
    }

    @GetMapping("/{almacenId}/{productoId}")
    public ResponseEntity<Inventario> obtener(
            @PathVariable Integer almacenId,
            @PathVariable Integer productoId
    ) {
        // CORRECCIÓN: Ahora podemos crear la llave compuesta porque importamos la entidad Inventario
        Inventario.InventarioId id = new Inventario.InventarioId(almacenId, productoId);

        return inventarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}