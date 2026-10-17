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

        return carritoRepository.save(c);
    }

    public Optional<Carrito> obtener(Long id) {
        return carritoRepository.findById(id);
    }

    public List<Carrito> listar() {
        return carritoRepository.findAll();
    }

    @Transactional
    public Carrito agregarProducto(Long id, CarritoProducto p) {
        Carrito c = buscarCarrito(id);
        validarProducto(p);
        c.addProducto(p);
        return carritoRepository.save(c);
    }

    @Transactional
    public Carrito eliminarProducto(Long id, Long idDetalle) {
        Carrito c = buscarCarrito(id);
        CarritoProducto p = buscarLinea(c, idDetalle);
        c.removeProducto(p);
        return carritoRepository.save(c);
    }

    @Transactional
    public Carrito cambiarCantidad(Long id, Long idDetalle, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("cantidad debe ser > 0");
        }
        Carrito c = buscarCarrito(id);
        CarritoProducto p = buscarLinea(c, idDetalle);
        p.setCantidad(cantidad);
        return carritoRepository.save(c);
    }

    @Transactional
    public Carrito vaciar(Long id) {
        Carrito c = buscarCarrito(id);
        new ArrayList<>(c.getProductos()).forEach(c::removeProducto);
        return carritoRepository.save(c);
    }

    private Carrito buscarCarrito(Long id) {
        return carritoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado"));
    }

    private CarritoProducto buscarLinea(Carrito c, Long idDetalle) {
        return c.getProductos().stream()
                .filter(x -> x.getIdDetalle().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en el carrito"));
    }

    private void validarProducto(CarritoProducto p) {
        if (p.getIdProducto() == null || p.getIdProducto() <= 0) {
            throw new IllegalArgumentException("idProducto es obligatorio en cada producto");
        }
        if (p.getCantidad() <= 0) {
            throw new IllegalArgumentException("cantidad debe ser > 0");
        }
    }
}
