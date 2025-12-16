package pe.cibertec.inkaproductos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String email;
    private String nombre;
    private List<String> roles;
}