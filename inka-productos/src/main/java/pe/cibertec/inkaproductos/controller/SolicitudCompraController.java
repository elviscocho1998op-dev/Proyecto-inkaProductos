package pe.cibertec.inkaproductos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.cibertec.inkaproductos.model.SolicitudCompra;
import pe.cibertec.inkaproductos.service.AlmacenService;
import pe.cibertec.inkaproductos.service.ProductoService;
import pe.cibertec.inkaproductos.service.SolicitudCompraService;

@Controller
@RequestMapping("/compras")
public class SolicitudCompraController {

    @Autowired
    private SolicitudCompraService solicitudService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private AlmacenService almacenService;


    @GetMapping("/peticiones")
    public String verPeticiones(Model model, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));

        if (isAdmin) {
            // Admin ve lo que tiene que aprobar
            model.addAttribute("solicitudes", solicitudService.listarPendientes());
            model.addAttribute("titulo", "Bandeja de Aprobaciones (Solo Admin)");
        } else {
            // User ve lo que ha pedido
            model.addAttribute("solicitudes", solicitudService.listarPorUsuario(auth.getName()));
            model.addAttribute("titulo", "Mis Solicitudes de Compra");
        }
        return "compras/lista_peticiones"; // Debes crear este HTML
    }


    @GetMapping("/nueva")
    public String nuevaSolicitud(Model model) {
        model.addAttribute("solicitud", new SolicitudCompra());
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("almacenes", almacenService.listar()); // Para elegir destino
        return "compras/form_compra"; // Debes crear este HTML
    }


    @PostMapping("/guardar")
    public String guardar(@ModelAttribute SolicitudCompra solicitud, Authentication auth, RedirectAttributes flash) {
        try {
            boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
            solicitud.setUsuarioSolicitante(auth.getName());

            solicitudService.crearSolicitud(solicitud, isAdmin);

            if (isAdmin) {
                flash.addFlashAttribute("success", "Compra realizada y stock actualizado correctamente.");
                return "redirect:/inventario"; // Admin vuelve al inventario
            } else {
                flash.addFlashAttribute("info", "Solicitud enviada al Supervisor/Gerente para aprobación.");
                return "redirect:/compras/peticiones"; // User va a ver sus peticiones
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/compras/nueva";
        }
    }


    @GetMapping("/aprobar/{id}")
    public String aprobar(@PathVariable Long id, Authentication auth, RedirectAttributes flash) {
        try {
            solicitudService.aprobarSolicitud(id, auth.getName());
            flash.addFlashAttribute("success", "Solicitud aprobada y stock ingresado.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al aprobar: " + e.getMessage());
        }
        return "redirect:/compras/peticiones";
    }


    @GetMapping("/rechazar/{id}")
    public String rechazar(@PathVariable Long id, RedirectAttributes flash) {
        solicitudService.rechazarSolicitud(id);
        flash.addFlashAttribute("warning", "Solicitud rechazada.");
        return "redirect:/compras/peticiones";
    }
}