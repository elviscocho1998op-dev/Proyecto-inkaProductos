package pe.cibertec.inkaproductos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "almacen")
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "almacen_id")
    private Integer almacenId;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoAlmacen tipo;

    @Column(length = 180)
    private String direccion;

    private boolean activo = true;

    @Column(name = "ultima_actualizacion", insertable = false, updatable = false)
    private LocalDateTime ultimaActualizacion;

    public enum TipoAlmacen {
        PRINCIPAL, SECUNDARIO, TRANSITO
    }
}