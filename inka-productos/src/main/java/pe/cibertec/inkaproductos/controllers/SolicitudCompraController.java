package pe.cibertec.inkaproductos.controllers;



import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.inkaproductos.models.SolicitudCompra;
import pe.cibertec.inkaproductos.services.SolicitudCompraService;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SolicitudCompraController {

    private final SolicitudCompraService solicitudService;

    @PostMapping("/procesar")
    public ResponseEntity<SolicitudCompra> crearCompra(
            @RequestBody SolicitudCompra solicitud,
            @RequestParam String rol) {
        // El parámetro 'rol' vendrá desde Angular según el usuario logueado
        return ResponseEntity.ok(solicitudService.procesarCompra(solicitud, rol));
    }
}