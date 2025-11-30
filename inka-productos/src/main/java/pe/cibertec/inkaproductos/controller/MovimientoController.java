package pe.cibertec.inkaproductos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.cibertec.inkaproductos.service.InventarioMovimientoService;

@Controller
@RequestMapping("/movimientos")
public class MovimientoController {

    @Autowired
    private InventarioMovimientoService movimientoService;

    @GetMapping
    public String listarHistorial(Model model) {
        // Usamos listarTodos() ya esta en service ayuda a listar  los movimientos pendiente de solucionar
        model.addAttribute("movimientos", movimientoService.listarTodos());
        model.addAttribute("titulo", "Historial de Movimientos (Kardex)");
        return "inventario/historial";
    }
}