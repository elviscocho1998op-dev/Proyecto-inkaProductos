package pe.cibertec.inkaproductos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin@inkaproductos.com")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user@inkaproductos.com")
                .password(encoder.encode("user123"))
                .roles("USER")
                .build();

        UserDetails ti = User.builder()
                .username("gestionti@inkaproductos.com")
                .password(encoder.encode("ti123"))
                .roles("TI")
                .build();

        return new InMemoryUserDetailsManager(admin, user, ti);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // 1. RUTA PÚBLICA PARA LOGIN
                        .requestMatchers("/api/auth/login").permitAll()

                        // 2. PERMITIR QUE TODOS VEAN LOS PRODUCTOS (SOLO LECTURA)
                        // Importa HttpMethod de org.springframework.http.HttpMethod
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()

                        // 3. SOLO TI PUEDE MODIFICAR (POST, PUT, DELETE)
                        .requestMatchers("/api/productos/**").hasRole("TI")

                        // Resto de tus reglas...
                        .requestMatchers("/api/tickets/**").hasAnyRole("ADMIN", "TI")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);

    }



}