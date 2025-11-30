package pe.cibertec.inkaproductos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;


@Data
@Entity
@Table(name = "producto_unidad")
public class ProductoUnidad {

    @EmbeddedId
    private ProductoUnidadId id;

    @ManyToOne
    @MapsId("productoId")
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @MapsId("unidadId")
    @JoinColumn(name = "unidad_id")
    private Unidad unidad;

    private BigDecimal factor;

    @Column(name = "es_base")
    private boolean esBase;

    @Embeddable
    @Data
    public static class ProductoUnidadId implements Serializable {
        private static final long serialVersionUID = 1L;

        @Column(name = "producto_id")
        private Integer productoId;

        @Column(name = "unidad_id")
        private Integer unidadId;

        public ProductoUnidadId() {}
        public ProductoUnidadId(Integer productoId, Integer unidadId) {
            this.productoId = productoId;
            this.unidadId = unidadId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProductoUnidadId that = (ProductoUnidadId) o;
            return Objects.equals(productoId, that.productoId) &&
                    Objects.equals(unidadId, that.unidadId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productoId, unidadId);
        }
    }
}