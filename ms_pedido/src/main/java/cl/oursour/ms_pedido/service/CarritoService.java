
package cl.oursour.ms_pedido.service;

import cl.oursour.ms_pedido.model.Carrito;
import cl.oursour.ms_pedido.model.CarritoProducto;
import cl.oursour.ms_pedido.repository.CarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;

    public CarritoService(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Transactional
    public Carrito crearCarrito(Carrito c) {
        if (c.getIdUsuario() == null || c.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("idUsuario es obligatorio");
        }

        List<CarritoProducto> productos = c.getProductos();
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("El carrito debe contener al menos un producto");
        }

        List<CarritoProducto> incoming = new ArrayList<>(productos);
        c.getProductos().clear();
        incoming.forEach(c::addProducto);

        for (CarritoProducto p : c.getProductos()) {
            validarProducto(p);
        }

    }
