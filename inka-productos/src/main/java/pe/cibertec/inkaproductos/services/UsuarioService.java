package pe.cibertec.inkaproductos.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.cibertec.inkaproductos.models.Usuario;
import pe.cibertec.inkaproductos.repositories.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario login(String email, String password) {
        // Buscamos al usuario por email
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        // Si existe, verificamos la clave con BCrypt (como pide tu rúbrica)
        if (usuario != null && passwordEncoder.matches(password, usuario.getPassword())) {
            return usuario;
        }
        return null; // Credenciales inválidas
    }
}