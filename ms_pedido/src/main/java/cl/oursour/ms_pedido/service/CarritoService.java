package cl.oursour.ms_pedido.service;

import cl.oursour.ms_pedido.dto.CarritoRequest;
import cl.oursour.ms_pedido.dto.CarritoResponse;
import cl.oursour.ms_pedido.dto.ProductoCarritoRequest;
import cl.oursour.ms_pedido.model.Carrito;
import cl.oursour.ms_pedido.model.CarritoProducto;
import cl.oursour.ms_pedido.repository.CarritoRepository;
import cl.oursour.ms_pedido.security.Autorizacion;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;

    public CarritoService(CarritoRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    // El dueño del carrito es siempre el usuario del token
    @Transactional
    public CarritoResponse crearCarrito(Long userId, CarritoRequest req) {
        Carrito c = new Carrito();
        c.setIdUsuario(userId);
        req.productos().forEach(p -> c.addProducto(crearLinea(p)));
        return CarritoResponse.desde(carritoRepository.save(c));
    }

    @Transactional(readOnly = true)
    public CarritoResponse obtener(Long id, Long userId, String rol) {
        return CarritoResponse.desde(buscarCarrito(id, userId, rol));
    }

    // El ADMIN ve todos los carritos; un consumidor solo los suyos
    @Transactional(readOnly = true)
    public List<CarritoResponse> listar(Long userId, String rol) {
        List<Carrito> carritos = Autorizacion.esAdmin(rol)
                ? carritoRepository.findAll()
                : carritoRepository.findByIdUsuario(userId);
        return carritos.stream().map(CarritoResponse::desde).toList();
    }

    @Transactional
    public CarritoResponse agregarProducto(Long id, ProductoCarritoRequest req, Long userId, String rol) {
        Carrito c = buscarCarrito(id, userId, rol);
        c.addProducto(crearLinea(req));
        return CarritoResponse.desde(carritoRepository.save(c));
    }

    @Transactional
    public CarritoResponse eliminarProducto(Long id, Long idDetalle, Long userId, String rol) {
        Carrito c = buscarCarrito(id, userId, rol);
        CarritoProducto p = buscarLinea(c, idDetalle);
        c.removeProducto(p);
        return CarritoResponse.desde(carritoRepository.save(c));
    }

    @Transactional
    public CarritoResponse cambiarCantidad(Long id, Long idDetalle, int cantidad, Long userId, String rol) {
        Carrito c = buscarCarrito(id, userId, rol);
        CarritoProducto p = buscarLinea(c, idDetalle);
        p.setCantidad(cantidad);
        return CarritoResponse.desde(carritoRepository.save(c));
    }

    @Transactional
    public CarritoResponse vaciar(Long id, Long userId, String rol) {
        Carrito c = buscarCarrito(id, userId, rol);
        new ArrayList<>(c.getProductos()).forEach(c::removeProducto);
        return CarritoResponse.desde(carritoRepository.save(c));
    }

    private Carrito buscarCarrito(Long id, Long userId, String rol) {
        Carrito c = carritoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrito no encontrado"));
        Autorizacion.exigirDuenoOAdmin(c.getIdUsuario(), userId, rol);
        return c;
    }

    private CarritoProducto buscarLinea(Carrito c, Long idDetalle) {
        return c.getProductos().stream()
                .filter(x -> x.getIdDetalle().equals(idDetalle))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto no encontrado en el carrito"));
    }

    private CarritoProducto crearLinea(ProductoCarritoRequest req) {
        CarritoProducto p = new CarritoProducto();
        p.setIdProducto(req.idProducto());
        p.setCantidad(req.cantidad());
        return p;
    }
}
