package pe.cibertec.inkaproductos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.cibertec.inkaproductos.model.Producto;
import pe.cibertec.inkaproductos.service.CategoriaService;
import pe.cibertec.inkaproductos.service.InventarioService;
import pe.cibertec.inkaproductos.service.ProductoService;

import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public String listar(Model model) {
        // CORRECCIÓN 1: El método se llama listar(), no listarTodos()
        model.addAttribute("productos", productoService.listar());
        model.addAttribute("titulo", "Listado de Productos");
        return "producto/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        // Cargamos categorías para el desplegable
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("titulo", "Nuevo Producto");
        return "producto/form";
    }

    // CORRECCIÓN 2: Cambiado Long a Integer en el @PathVariable
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes flash) {
        Optional<Producto> optionalProducto = productoService.obtenerPorId(id);

        if (optionalProducto.isEmpty()) {
            flash.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/productos";
        }

        model.addAttribute("producto", optionalProducto.get());
        model.addAttribute("categorias", categoriaService.listar());
        model.addAttribute("titulo", "Editar Producto");
        return "producto/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto, RedirectAttributes flash) {
        try {
            // Guardamos el producto primero para obtener su ID
            Producto productoGuardado = productoService.guardar(producto);

            // Lógica para stock inicial (Solo si es nuevo y tiene cantidadInicial)
            // Nota: cantidadInicial es @Transient en la entidad, sirve solo para este paso
            if (producto.getCantidadInicial() != null &&
                    producto.getCantidadInicial().compareTo(BigDecimal.ZERO) > 0) {

                // Asumimos Almacén Principal (ID 1)
                inventarioService.actualizarStock(
                        1,
                        productoGuardado.getProductoId(),
                        producto.getCantidadInicial()
                );
            }

            flash.addFlashAttribute("success", "Producto guardado con éxito.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
            return "redirect:/productos"; // O volver al form si prefieres
        }
        return "redirect:/productos";
    }

    // CORRECCIÓN 3: Cambiado Long a Integer
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
        if (productoService.obtenerPorId(id).isEmpty()) {
            flash.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/productos";
        }
        try {
            productoService.eliminar(id);
            flash.addFlashAttribute("success", "Producto eliminado con éxito.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error: El producto podría tener stock o movimientos asociados.");
        }
        return "redirect:/productos";
    }
}