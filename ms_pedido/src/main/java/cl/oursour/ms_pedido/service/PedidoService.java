package cl.oursour.ms_pedido.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.oursour.ms_pedido.model.DireccionDespacho;
import cl.oursour.ms_pedido.model.Pedido;
import cl.oursour.ms_pedido.model.PedidoProducto;
import cl.oursour.ms_pedido.repository.PedidoRepository;
import jakarta.transaction.Transactional;

@Service 
public class PedidoService {

     private static final int MONTO_ENVIO_GRATIS = 50000;
    private static final int COSTO_DESPACHO = 3990;

    private final PedidoRepository pedidoRepository;
    private final DireccionService direccionService;

    public PedidoService(PedidoRepository pedidoRepository, DireccionService direccionService) {
        this.pedidoRepository = pedidoRepository;
        this.direccionService = direccionService;
    }

    @Transactional
    public Pedido crearPedido(Pedido p) {
        if (p.getIdUsuario() == null || p.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("idUsuario es obligatorio");
        }
        validarDireccion(p.getIdDireccion(), p.getIdUsuario());

        List<PedidoProducto> productos = p.getProductos();
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un producto");
        }

        List<PedidoProducto> incoming = new ArrayList<>(productos);
        p.getProductos().clear();
        incoming.forEach(p::addProducto);

        int subtotal = 0;
        for (PedidoProducto d : p.getProductos()) {
            if (d.getIdProducto() == null || d.getIdProducto() <= 0) {
                throw new IllegalArgumentException("idProducto es obligatorio en cada producto");
            }
            if (d.getCantidad() <= 0) {
                throw new IllegalArgumentException("cantidad debe ser > 0");
            }
            if (d.getPrecioUnitario() <= 0) {
                throw new IllegalArgumentException("precioUnitario debe ser mayor que 0");
            }
            subtotal += d.getPrecioUnitario() * d.getCantidad();
        }

        int despacho = calcularDespacho(subtotal);
        p.setSubtotal(subtotal);
        p.setCostoDespacho(despacho);
        p.setMontoTotal(subtotal + despacho);
        p.setEstado("PENDIENTE_PAGO");

        return pedidoRepository.save(p);
    }

    public Optional<Pedido> obtener(Long id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @Transactional
    public Pedido actualizarEstado(Long id, String estado) {
        Pedido p = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        p.setEstado(estado);
        return pedidoRepository.save(p);
    }

    public boolean validarPedido(Long id) {
        return pedidoRepository.existsById(id);
    }

    private int calcularDespacho(int subtotal) {
        return subtotal >= MONTO_ENVIO_GRATIS ? 0 : COSTO_DESPACHO;
    }

    private void validarDireccion(Long idDireccion, Long idUsuario) {
        if (idDireccion == null || idDireccion <= 0) {
            throw new IllegalArgumentException("idDireccion es obligatorio");
        }
        DireccionDespacho direccion = direccionService.buscarPorId(idDireccion)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no existe"));
        if (!idUsuario.equals(direccion.getIdUsuario())) {
            throw new IllegalArgumentException("La dirección no pertenece al usuario");
        }

    }

  
}
