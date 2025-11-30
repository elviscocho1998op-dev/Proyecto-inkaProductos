package pe.cibertec.inkaproductos.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.inkaproductos.model.Role;
import pe.cibertec.inkaproductos.model.Usuario;
import pe.cibertec.inkaproductos.repository.RoleRepository;
import pe.cibertec.inkaproductos.repository.UsuarioRepository;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        Role adminRole = findOrCreateRole("ADMIN");
        Role userRole = findOrCreateRole("USER");

        // Crear Admin por defecto si no existe
        if (usuarioRepository.findByEmail("admin@inka.com") == null) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setEmail("admin@inka.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            admin.setRoles(new HashSet<>(Set.of(adminRole)));
            usuarioRepository.save(admin);
            System.out.println(">> Usuario ADMIN creado: admin@inka.com / admin123");
        }


        if (usuarioRepository.findByEmail("logistica@inka.com") == null) {
            Usuario user = new Usuario();
            user.setNombre("Usuario Logística");
            user.setEmail("logistica@inka.com");
            user.setPasswordHash(passwordEncoder.encode("user123"));
            user.setEnabled(true);
            user.setRoles(new HashSet<>(Set.of(userRole)));
            usuarioRepository.save(user);
            System.out.println(">> Usuario USER creado: logistica@inka.com / user123");
        }
    }

    private Role findOrCreateRole(String roleName) {
        return roleRepository.findByNombre(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setNombre(roleName);
                    return roleRepository.save(newRole);
                });
    }
}