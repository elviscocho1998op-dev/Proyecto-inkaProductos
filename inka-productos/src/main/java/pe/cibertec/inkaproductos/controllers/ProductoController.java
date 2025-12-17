package pe.cibertec.inkaproductos.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.inkaproductos.dto.TransaccionDTO;
import pe.cibertec.inkaproductos.models.Producto;
import pe.cibertec.inkaproductos.models.SolicitudCompra;
import pe.cibertec.inkaproductos.services.ProductoService;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public List<Producto> listar() {
        return productoService.listarTodos();
    }

    // Este método solo debe ser llamado por el perfil TI según tu lógica
    @PostMapping
    public ResponseEntity<Producto> guardar(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.guardar(producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtrar")
    public List<Producto> filtrarProductos(
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Integer almacenId
    ) {
        return productoService.filtrar(categoriaId, almacenId);
    }

    @PostMapping("/transaccion")
    public ResponseEntity<?> procesar(@RequestBody TransaccionDTO datos) {
        try {
            productoService.procesarTransaccion(datos);
            return ResponseEntity.ok().body("{\"message\": \"Operación exitosa: Historial y Stock actualizados\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error: " + e.getMessage() + "\"}");
        }
    }


    @GetMapping("/transaccion/historial")
    public ResponseEntity<List<SolicitudCompra>> obtenerHistorial() {
        return ResponseEntity.ok(productoService.listarHistorial());
    }
}