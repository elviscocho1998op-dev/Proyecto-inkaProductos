package pe.cibertec.inkaproductos.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.inkaproductos.dto.TransaccionDTO;
import pe.cibertec.inkaproductos.services.SolicitudService;
import pe.cibertec.inkaproductos.models.SolicitudCompra;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    // USER crea solicitud (NO mueve stock)
    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody TransaccionDTO dto) {
        try {
            return ResponseEntity.ok(solicitudService.crearSolicitud(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // USER mira solo sus solicitudes
    @GetMapping("/mias")
    public List<SolicitudCompra> misSolicitudes(@RequestParam String email) {
        return solicitudService.listarPorUsuario(email);
    }

    @GetMapping("/pendientes")
    public List<SolicitudCompra> pendientes() {
        return solicitudService.listarPendientes();
    }
    @PostMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(solicitudService.aprobarSolicitud(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
