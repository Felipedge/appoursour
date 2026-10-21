package cl.oursour.ms_pedido.service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.oursour.ms_pedido.client.CatalogoClient;
import cl.oursour.ms_pedido.dto.PedidoRequest;
import cl.oursour.ms_pedido.dto.PedidoResponse;
import cl.oursour.ms_pedido.model.CarritoProducto;
import cl.oursour.ms_pedido.model.EstadoPedido;
import cl.oursour.ms_pedido.model.Pedido;
import cl.oursour.ms_pedido.model.PedidoProducto;
import cl.oursour.ms_pedido.repository.PedidoRepository;
import cl.oursour.ms_pedido.security.Autorizacion;

@Service
public class PedidoService {

    private static final BigDecimal MONTO_ENVIO_GRATIS = new BigDecimal("50000");
    private static final BigDecimal COSTO_DESPACHO = new BigDecimal("3990");

    
    private static final Set<EstadoPedido> PAGABLES = Set.of(EstadoPedido.PENDIENTE_PAGO, EstadoPedido.PAGO_RECHAZADO);

    private final PedidoRepository pedidoRepository;
    private final DireccionService direccionService;
    private final CarritoService carritoService;
    private final CatalogoClient catalogoClient;

    public PedidoService(PedidoRepository pedidoRepository, DireccionService direccionService,
                         CarritoService carritoService, CatalogoClient catalogoClient) {
        this.pedidoRepository = pedidoRepository;
        this.direccionService = direccionService;
        this.carritoService = carritoService;
        this.catalogoClient = catalogoClient;
    }

    
    @Transactional
    public PedidoResponse crearPedido(Long userId, PedidoRequest req) {
        direccionService.buscarDelUsuario(req.idDireccion(), userId);

        boolean desdeCarrito = req.productos() == null || req.productos().isEmpty();
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        if (desdeCarrito) {
            for (CarritoProducto c : carritoService.productosDelUsuario(userId)) {
                cantidades.merge(c.getIdProducto(), c.getCantidad(), Integer::sum);
            }
            if (cantidades.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
            }
        } else {
            req.productos().forEach(l -> cantidades.merge(l.idProducto(), l.cantidad(), Integer::sum));
        }

        Map<Long, CatalogoClient.Producto> catalogo = catalogoClient.productos(cantidades.keySet());

        Pedido p = new Pedido();
        p.setIdUsuario(userId);
        p.setIdDireccion(req.idDireccion());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> linea : cantidades.entrySet()) {
            CatalogoClient.Producto producto = catalogo.get(linea.getKey());
            if (producto == null || !Boolean.TRUE.equals(producto.activo())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no disponible: " + linea.getKey());
            }
            if (producto.stockSimulado() == null || producto.stockSimulado() < linea.getValue()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para "
                        + producto.nombreComercial() + " (disponible: " + producto.stockSimulado() + ")");
            }

            PedidoProducto d = new PedidoProducto();
            d.setIdProducto(linea.getKey());
            d.setCantidad(linea.getValue());
            d.setPrecioUnitario(producto.precioUnitario());
            p.addProducto(d);
            subtotal = subtotal.add(producto.precioUnitario().multiply(BigDecimal.valueOf(linea.getValue())));
        }

        BigDecimal despacho = calcularDespacho(subtotal);
        p.setSubtotal(subtotal);
        p.setCostoDespacho(despacho);
        p.setMontoTotal(subtotal.add(despacho));
        p.setEstado(EstadoPedido.PENDIENTE_PAGO);

        PedidoResponse respuesta = PedidoResponse.desde(pedidoRepository.save(p));
        if (desdeCarrito) {
            carritoService.vaciarSiExiste(userId);
        }
        return respuesta;
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtener(Long id, Long userId, String rol) {
        Pedido p = buscarPedido(id);
        Autorizacion.exigirDuenoOAdmin(p.getIdUsuario(), userId, rol);
        return PedidoResponse.desde(p);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(Long userId, String rol) {
        List<Pedido> pedidos = Autorizacion.esAdmin(rol)
                ? pedidoRepository.findAll()
                : pedidoRepository.findByIdUsuario(userId);
        return pedidos.stream().map(PedidoResponse::desde).toList();
    }

    @Transactional
    public PedidoResponse actualizarEstado(Long id, EstadoPedido estado) {
        Pedido p = buscarPedido(id);
        p.setEstado(estado);
        return PedidoResponse.desde(pedidoRepository.save(p));
    }

    // Lo llama ms_pago: aprobado descuenta stock en catálogo y pasa a SOLICITADO; rechazado pasa a PAGO_RECHAZADO
    @Transactional
    public PedidoResponse registrarResultadoPago(Long id, boolean aprobado) {
        Pedido p = buscarPedido(id);
        if (!PAGABLES.contains(p.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El pedido no está pendiente de pago (estado: " + p.getEstado() + ")");
        }
        if (aprobado) {
            catalogoClient.descontarStock(p.getProductos().stream()
                    .map(d -> new CatalogoClient.Item(d.getIdProducto(), d.getCantidad()))
                    .toList());
            p.setEstado(EstadoPedido.SOLICITADO);
        } else {
            p.setEstado(EstadoPedido.PAGO_RECHAZADO);
        }
        return PedidoResponse.desde(pedidoRepository.save(p));
    }

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    private BigDecimal calcularDespacho(BigDecimal subtotal) {
        return subtotal.compareTo(MONTO_ENVIO_GRATIS) >= 0 ? BigDecimal.ZERO : COSTO_DESPACHO;
    }

}
