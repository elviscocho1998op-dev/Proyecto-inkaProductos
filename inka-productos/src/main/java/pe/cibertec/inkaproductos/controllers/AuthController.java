package pe.cibertec.inkaproductos.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.cibertec.inkaproductos.dto.LoginRequest;
import pe.cibertec.inkaproductos.models.Usuario;
import pe.cibertec.inkaproductos.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // Para Angular
public class AuthController {

    private final UsuarioService usuarioService;

    // Constructor manual como el del profesor
    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Usamos el método login del service
        Usuario usuario = usuarioService.login(loginRequest.getEmail(), loginRequest.getPassword());

        if (usuario == null) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }

        // Devolvemos el objeto usuario (Spring lo convierte a JSON automáticamente)
        return ResponseEntity.ok(usuario);
    }
}