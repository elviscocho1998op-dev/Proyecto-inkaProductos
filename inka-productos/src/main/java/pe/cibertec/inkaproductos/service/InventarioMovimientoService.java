package pe.cibertec.inkaproductos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.inkaproductos.model.InventarioMovimiento;
import pe.cibertec.inkaproductos.repository.InventarioMovimientoRepository;
import pe.cibertec.inkaproductos.repository.UnidadRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioMovimientoService {

    @Autowired
    private InventarioMovimientoRepository movimientoRepository;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private UnidadRepository unidadRepository;

    @Transactional
    public InventarioMovimiento guardar(InventarioMovimiento movimiento) {

        if (movimiento.getCantidad() == null || movimiento.getCantidad().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("La cantidad no puede ser cero.");
        }

        movimiento.setFechaMovimiento(LocalDateTime.now());


        InventarioMovimiento guardado = movimientoRepository.save(movimiento);

        BigDecimal cantidadParaStock = movimiento.getTipoMovimiento() == InventarioMovimiento.TipoMovimiento.SALIDA
                ? movimiento.getCantidad().negate()
                : movimiento.getCantidad();

        inventarioService.actualizarStock(
                movimiento.getAlmacen().getAlmacenId(),
                movimiento.getProducto().getProductoId(),
                cantidadParaStock
        );

        return guardado;
    }

    public List<InventarioMovimiento> listarTodos() {
        return movimientoRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaMovimiento"));
    }
}