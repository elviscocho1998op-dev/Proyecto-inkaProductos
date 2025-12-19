package pe.cibertec.inkaproductos.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.cibertec.inkaproductos.models.*;
import pe.cibertec.inkaproductos.repositories.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository repoMovimiento;
    private final MovimientoDetalleRepository repoDetalle;
    private final AlmacenRepository repoAlmacen;
    private final ProductoRepository repoProducto;

    public Movimiento registrarMovimiento(
            String usuario,
            Integer origenId,
            Integer destinoId,
            List<MovimientoDetalle> detalles
    ) {
        Movimiento mov = Movimiento.builder()
                .usuario(usuario)
                .origen(repoAlmacen.findById(origenId).orElse(null))
                .destino(repoAlmacen.findById(destinoId).orElse(null))
                .build();

        mov = repoMovimiento.save(mov);

        for (MovimientoDetalle d : detalles) {
            d.setMovimiento(mov);
            repoDetalle.save(d);
        }

        return mov;
    }

    public List<Movimiento> listarHistorial() {
        return repoMovimiento.findAll();
    }

    public Movimiento crearSolicitudPendiente(
            String usuario,
            Integer origenId,
            Integer destinoId,
            List<MovimientoDetalle> detalles
    ) {
        Movimiento mov = Movimiento.builder()
                .usuario(usuario)
                .origen(repoAlmacen.findById(origenId).orElse(null))
                .destino(repoAlmacen.findById(destinoId).orElse(null))
                .estado(Movimiento.EstadoMovimiento.PENDIENTE)
                .build();

        mov = repoMovimiento.save(mov);

        for (MovimientoDetalle d : detalles) {
            d.setMovimiento(mov);
            repoDetalle.save(d);
        }

        return mov;
    }
}
