package cl.oursour.ms_pedido.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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

    }
    

  
}
