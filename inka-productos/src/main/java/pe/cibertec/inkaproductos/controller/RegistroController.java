package pe.cibertec.inkaproductos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.cibertec.inkaproductos.model.Usuario;
import pe.cibertec.inkaproductos.service.UsuarioService;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro/guardar")
    public String registrarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes flash) {
        try {

            usuarioService.guardar(usuario);
            flash.addFlashAttribute("success", "¡Cuenta creada con éxito! Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/registro";
        }
    }
}