package pe.cibertec.inkaproductos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Entity
@Table(name = "inventario")
public class Inventario {

    @EmbeddedId
    private InventarioId id;

    @ManyToOne
    @MapsId("almacenId")
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;

    @ManyToOne
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private BigDecimal cantidad;

    @Column(name = "stock_min")
    private BigDecimal stockMin;

    @Column(name = "stock_max")
    private BigDecimal stockMax;

    @Column(name = "ultima_actualizacion", insertable = false, updatable = false)
    private LocalDateTime ultimaActualizacion;

    @Embeddable
    @Data
    public static class InventarioId implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "almacen_id")
        private Integer almacenId;

        @Column(name = "producto_id")
        private Integer productoId;

        public InventarioId() {}
        public InventarioId(Integer almacenId, Integer productoId) {
            this.almacenId = almacenId;
            this.productoId = productoId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InventarioId that = (InventarioId) o;
            return Objects.equals(almacenId, that.almacenId) &&
                    Objects.equals(productoId, that.productoId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(almacenId, productoId);
        }
    }
}