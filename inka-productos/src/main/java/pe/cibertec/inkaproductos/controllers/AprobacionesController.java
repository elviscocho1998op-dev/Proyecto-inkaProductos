package pe.cibertec.inkaproductos.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.inkaproductos.models.SolicitudCompra;
import pe.cibertec.inkaproductos.services.AprobacionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/aprobaciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AprobacionesController {

    private final AprobacionService aprobacionService;

    @GetMapping("/pendientes")
    public List<SolicitudCompra> pendientes() {
        return aprobacionService.listarPendientes();
    }


    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(@PathVariable Integer id) {
        return ResponseEntity.ok(aprobacionService.rechazar(id));
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(@PathVariable Integer id) {

        try {
            SolicitudCompra aprobada = aprobacionService.aprobar(id);

            return ResponseEntity.ok(
                    Map.of("mensaje", "Solicitud aprobada", "solicitud", aprobada)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }

}
