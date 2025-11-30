package pe.cibertec.inkaproductos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.cibertec.inkaproductos.service.AlmacenService; // Importación necesaria
import pe.cibertec.inkaproductos.service.CategoriaService;
import pe.cibertec.inkaproductos.service.InventarioService;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private CategoriaService categoriaService;

    // ✅ SOLUCIÓN AL ERROR: Declaración e inyección del servicio Almacen
    @Autowired
    private AlmacenService almacenService;

    @GetMapping
    public String listarInventario(
            // Captura los parámetros del filtro de la URL
            @RequestParam(required = false) Integer almacenId,
            @RequestParam(required = false) Integer categoriaId,
            Model model
    ) {

        model.addAttribute("inventarios", inventarioService.listar(almacenId, categoriaId));


        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("almacenes", almacenService.listar());


        model.addAttribute("catSeleccionada", categoriaId);
        model.addAttribute("almSeleccionado", almacenId);

        return "inventario/lista";
    }
}