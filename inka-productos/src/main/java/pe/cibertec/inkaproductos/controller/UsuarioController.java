package pe.cibertec.inkaproductos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.cibertec.inkaproductos.model.Role;
import pe.cibertec.inkaproductos.model.Usuario;
import pe.cibertec.inkaproductos.repository.RoleRepository;
import pe.cibertec.inkaproductos.service.UsuarioService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuario/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("rolesDisponibles", roleRepository.findAll());
        return "usuario/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
                          @RequestParam(value = "selectedRoles", required = false) List<Long> selectedRoles,
                          RedirectAttributes flash) {
        try {
            Set<Role> roles = new HashSet<>();
            if (selectedRoles != null) {
                for (Long rId : selectedRoles) {
                    roleRepository.findById(rId).ifPresent(roles::add);
                }
            }
            usuario.setRoles(roles);
            usuarioService.guardar(usuario);
            flash.addFlashAttribute("success", "Usuario guardado.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }


    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            usuarioService.eliminar(id);
            flash.addFlashAttribute("success", "Usuario eliminado con éxito.");
        } catch (Exception e) {

            flash.addFlashAttribute("error", "No se puede eliminar el usuario: Tiene historial de compras o movimientos registrados.");
        }
        return "redirect:/usuarios";
    }

// ... (resto del código)

}