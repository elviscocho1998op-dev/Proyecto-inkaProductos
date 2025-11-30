package pe.cibertec.inkaproductos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "unidad")
public class Unidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unidad_id")
    private Integer unidadId;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "factor_base")
    private BigDecimal factorBase = BigDecimal.ONE;

    @Column(name = "es_base")
    private boolean esBase = true;


    @Column(unique = true)
    private String abreviatura;

    private Boolean activo = true;

    @Column(name = "ultima_actualizacion", insertable = false, updatable = false)
    private LocalDateTime ultimaActualizacion;
}