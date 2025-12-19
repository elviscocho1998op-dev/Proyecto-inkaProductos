package pe.cibertec.inkaproductos.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movimiento_detalle")
@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MovimientoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movimiento_id")
    private Movimiento movimiento;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Double cantidad;
}
