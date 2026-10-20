package cl.oursour.ms_pedido.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.oursour.ms_pedido.dto.PedidoRequest;
import cl.oursour.ms_pedido.dto.PedidoResponse;
import cl.oursour.ms_pedido.dto.ProductoPedidoRequest;
import cl.oursour.ms_pedido.model.EstadoPedido;
import cl.oursour.ms_pedido.model.Pedido;
import cl.oursour.ms_pedido.model.PedidoProducto;
import cl.oursour.ms_pedido.repository.PedidoRepository;
import cl.oursour.ms_pedido.security.Autorizacion;

@Service
public class PedidoService {

    private static final BigDecimal MONTO_ENVIO_GRATIS = new BigDecimal("50000");
    private static final BigDecimal COSTO_DESPACHO = new BigDecimal("3990");

    private final PedidoRepository pedidoRepository;
    private final DireccionService direccionService;

    public PedidoService(PedidoRepository pedidoRepository, DireccionService direccionService) {
        this.pedidoRepository = pedidoRepository;
        this.direccionService = direccionService;
    }

    
    @Transactional
    public PedidoResponse crearPedido(Long userId, PedidoRequest req) {
        direccionService.buscarDelUsuario(req.idDireccion(), userId);

        Pedido p = new Pedido();
        p.setIdUsuario(userId);
        p.setIdDireccion(req.idDireccion());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (ProductoPedidoRequest linea : req.productos()) {
            PedidoProducto d = new PedidoProducto();
            d.setIdProducto(linea.idProducto());
            d.setCantidad(linea.cantidad());
            d.setPrecioUnitario(linea.precioUnitario());
            p.addProducto(d);
            subtotal = subtotal.add(linea.precioUnitario().multiply(BigDecimal.valueOf(linea.cantidad())));
        }

        BigDecimal despacho = calcularDespacho(subtotal);
        p.setSubtotal(subtotal);
        p.setCostoDespacho(despacho);
        p.setMontoTotal(subtotal.add(despacho));
        p.setEstado(EstadoPedido.PENDIENTE_PAGO);

        return PedidoResponse.desde(pedidoRepository.save(p));
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

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    private BigDecimal calcularDespacho(BigDecimal subtotal) {
        return subtotal.compareTo(MONTO_ENVIO_GRATIS) >= 0 ? BigDecimal.ZERO : COSTO_DESPACHO;
    }

}
