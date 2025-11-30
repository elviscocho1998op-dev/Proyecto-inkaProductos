package pe.cibertec.inkaproductos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.inkaproductos.model.Role;
import pe.cibertec.inkaproductos.model.Usuario;
import pe.cibertec.inkaproductos.repository.RoleRepository;
import pe.cibertec.inkaproductos.repository.UsuarioRepository;

import java.util.HashSet;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        // 1. LOGICA DE CONTRASEÑA (Para Editar)(pendiente correcion)
        if (usuario.getUsuarioId() != null) {

            Usuario existente = usuarioRepository.findById(usuario.getUsuarioId()).orElseThrow();


            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                usuario.setPasswordHash(existente.getPasswordHash());
            } else {
                // Spara encriptar
                usuario.setPasswordHash(passwordEncoder.encode(usuario.getPassword()));
            }
        } else {

            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuario.setPasswordHash(passwordEncoder.encode(usuario.getPassword()));
            } else {
                throw new IllegalArgumentException("La contraseña es obligatoria.");
            }
        }


        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            if (usuario.getRoles() == null) usuario.setRoles(new HashSet<>());
            Role userRole = roleRepository.findByNombre("USER")
                    .orElseGet(() -> {
                        Role r = new Role(); r.setNombre("USER"); return roleRepository.save(r);
                    });
            usuario.getRoles().add(userRole);
        }

        return usuarioRepository.save(usuario);
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }


    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}