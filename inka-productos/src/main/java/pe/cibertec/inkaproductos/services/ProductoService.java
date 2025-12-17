package pe.cibertec.inkaproductos.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cibertec.inkaproductos.dto.ItemCarritoDTO;
import pe.cibertec.inkaproductos.dto.TransaccionDTO;
import pe.cibertec.inkaproductos.models.*;
import pe.cibertec.inkaproductos.repositories.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final SolicitudCompraRepository solicitudRepo;
    private final SolicitudDetalleRepository detalleRepo;
    private final InventarioRepository inventarioRepo;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public List<SolicitudCompra> listarHistorial() {
        return solicitudRepo.findAll();
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    public List<Producto> filtrar(Integer categoriaId, Integer almacenId) {
        Integer catId = (categoriaId != null && categoriaId > 0) ? categoriaId : null;
        Integer almId = (almacenId != null && almacenId > 0) ? almacenId : null;
        return productoRepository.filtrarProductos(catId, almId);
    }

    @Transactional
    public void procesarTransaccion(TransaccionDTO datos) {
        // 1. Crear la Cabecera de la Solicitud
        SolicitudCompra cabecera = new SolicitudCompra();

        Almacen org = new Almacen();
        org.setAlmacenId(datos.getOrigenId());

        Almacen dest = new Almacen();
        dest.setAlmacenId(datos.getDestinoId());

        cabecera.setOrigen(org);
        cabecera.setDestino(dest);
        cabecera.setUsuarioSolicitante(datos.getUsuarioEmail() != null ? datos.getUsuarioEmail() : "Sistema");

        // --- REGLA DE NEGOCIO SEGÚN PERFIL ---
        // Si el DTO dice que esAdmin=true, la compra nace APROBADA.
        // Si esAdmin=false (Perfil USER), nace PENDIENTE.
        boolean esAprobadaDirecta = datos.isEsAdmin();
        cabecera.setEstado(esAprobadaDirecta ? "APROBADA" : "PENDIENTE");

        // 2. Procesar los detalles
        List<SolicitudCompraDetalle> detalles = datos.getItems().stream().map(item -> {
            SolicitudCompraDetalle detalle = new SolicitudCompraDetalle();

            Producto p = new Producto();
            p.setProductoId(item.getProductoId());

            detalle.setProducto(p);
            detalle.setCantidad(item.getCantidad().doubleValue());
            detalle.setSolicitud(cabecera); // Relación bidireccional

            // --- ACTUALIZACIÓN DE STOCK CONDICIONAL ---
            // Solo se mueve el inventario si la solicitud se marca como APROBADA (ADMIN).
            // Si es USER, este bloque se ignora y el stock queda intacto hasta que el ADMIN apruebe después.
            if (esAprobadaDirecta) {
                actualizarStock(datos.getOrigenId(), item.getProductoId(), -item.getCantidad().doubleValue());
                actualizarStock(datos.getDestinoId(), item.getProductoId(), item.getCantidad().doubleValue());
            }

            return detalle;
        }).collect(Collectors.toList());

        cabecera.setDetalles(detalles);

        // 3. Persistencia en Base de Datos
        solicitudRepo.save(cabecera);
    }

    private void actualizarStock(Integer almacenId, Integer productoId, Double cantidad) {
        // Llama al query nativo o JPQL de tu InventarioRepository
        inventarioRepo.actualizarCantidad(almacenId, productoId, cantidad);
    }
}