package pe.cibertec.inkaproductos.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "movimiento")
@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;

    private String usuario;

    @ManyToOne
    @JoinColumn(name = "origen_id")
    private Almacen origen;

    @ManyToOne
    @JoinColumn(name = "destino_id")
    private Almacen destino;

    @Enumerated(EnumType.STRING)
    private EstadoMovimiento estado;

    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL)
    private List<MovimientoDetalle> detalles;

    @PrePersist
    public void prePersist() {
        fecha = LocalDateTime.now();
        estado = EstadoMovimiento.APROBADO; // por defecto (ADMIN)
    }


    public enum EstadoMovimiento {
        APROBADO, RECHAZADO, PENDIENTE
    }
}
