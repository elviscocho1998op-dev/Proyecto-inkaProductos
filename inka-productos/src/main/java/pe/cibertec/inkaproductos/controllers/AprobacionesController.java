package pe.cibertec.inkaproductos.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.inkaproductos.models.SolicitudCompra;
import pe.cibertec.inkaproductos.services.AprobacionService;

import java.util.List;

@RestController
@RequestMapping("/api/aprobaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AprobacionesController {

    private final AprobacionService aprobacionService;

    // ADMIN: ver pendientes
    @GetMapping("/pendientes")
    public ResponseEntity<List<SolicitudCompra>> pendientes() {
        return ResponseEntity.ok(aprobacionService.listarPendientes());
    }

    // ADMIN: aprobar (UPDATE + mueve stock)
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<SolicitudCompra> aprobar(@PathVariable Integer id) {
        return ResponseEntity.ok(aprobacionService.aprobar(id));
    }

    // ADMIN: rechazar (UPDATE, no mueve stock)
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudCompra> rechazar(@PathVariable Integer id) {
        return ResponseEntity.ok(aprobacionService.rechazar(id));
    }
}
