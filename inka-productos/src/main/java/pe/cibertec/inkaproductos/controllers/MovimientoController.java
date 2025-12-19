package pe.cibertec.inkaproductos.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.inkaproductos.models.Movimiento;
import pe.cibertec.inkaproductos.services.MovimientoService;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MovimientoController {

    private final MovimientoService movimientoRepo;

    @GetMapping("/movimientos")
    public List<Movimiento> listarMovimientos() {
        return movimientoRepo.listarHistorial();
    }

}
