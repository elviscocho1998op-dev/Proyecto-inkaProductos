package pe.cibertec.inkaproductos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "solicitud_compra")
public class SolicitudCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column(name = "usuario_solicitante", nullable = false)
    private String usuarioSolicitante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @PrePersist
    public void prePersist() {
        fechaSolicitud = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoSolicitud.PENDIENTE;
        }
    }

    public enum EstadoSolicitud {
        PENDIENTE, APROBADO, RECHAZADO
    }
}