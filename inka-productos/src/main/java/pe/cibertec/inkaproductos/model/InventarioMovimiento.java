package pe.cibertec.inkaproductos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventario_movimiento")
public class InventarioMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventario_movimiento_id")
    private Long inventarioMovimientoId;

    @Column(name = "fecha_movimiento", insertable = false, updatable = false)
    private LocalDateTime fechaMovimiento;

    @ManyToOne
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento")
    private TipoMovimiento tipoMovimiento;

    private BigDecimal cantidad;

    @Column(name = "costo_unitario")
    private BigDecimal costoUnitario;

    private String referencia;
    private String usuario;

    public enum TipoMovimiento {
        ENTRADA, SALIDA, AJUSTE
    }
}