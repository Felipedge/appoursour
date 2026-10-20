package cl.oursour.ms_pedido.service;

import cl.oursour.ms_pedido.client.CatalogoClient;
import cl.oursour.ms_pedido.dto.CarritoResponse;
import cl.oursour.ms_pedido.dto.ProductoCarritoRequest;
import cl.oursour.ms_pedido.model.Carrito;
import cl.oursour.ms_pedido.model.CarritoProducto;
import cl.oursour.ms_pedido.repository.CarritoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CatalogoClient catalogoClient;

    public CarritoService(CarritoRepository carritoRepository, CatalogoClient catalogoClient) {
        this.carritoRepository = carritoRepository;
        this.catalogoClient = catalogoClient;
    }

    @Transactional(readOnly = true)
    public CarritoResponse obtener(Long userId) {
        return buscarDelUsuario(userId).map(CarritoResponse::desde).orElseGet(() -> CarritoResponse.vacio(userId));
    }

    
    public CarritoResponse agregarProducto(Long userId, ProductoCarritoRequest req) {
        validarProductoDisponible(req.idProducto());

        Carrito c = buscarDelUsuario(userId).orElseGet(() -> {
            Carrito nuevo = new Carrito();
            nuevo.setIdUsuario(userId);
            return nuevo;
        });

        buscarLinea(c, req.idProducto()).ifPresentOrElse(
                p -> p.setCantidad(p.getCantidad() + req.cantidad()),
                () -> {
                    CarritoProducto p = new CarritoProducto();
                    p.setIdProducto(req.idProducto());
                    p.setCantidad(req.cantidad());
                    c.addProducto(p);
                });
        return CarritoResponse.desde(carritoRepository.save(c));
    }

    @Transactional
    public CarritoResponse cambiarCantidad(Long userId, Long idProducto, int cantidad) {
        Carrito c = exigirCarrito(userId);
        exigirLinea(c, idProducto).setCantidad(cantidad);
        return CarritoResponse.desde(carritoRepository.save(c));
    }

    @Transactional
    public CarritoResponse eliminarProducto(Long userId, Long idProducto) {
        Carrito c = exigirCarrito(userId);
        c.removeProducto(exigirLinea(c, idProducto));
        return CarritoResponse.desde(carritoRepository.save(c));
    }

    @Transactional
    public CarritoResponse vaciar(Long userId) {
        Carrito c = exigirCarrito(userId);
        new ArrayList<>(c.getProductos()).forEach(c::removeProducto);
        return CarritoResponse.desde(carritoRepository.save(c));
    }

    
    List<CarritoProducto> productosDelUsuario(Long userId) {
        return buscarDelUsuario(userId).map(c -> List.copyOf(c.getProductos())).orElse(List.of());
    }

    void vaciarSiExiste(Long userId) {
        buscarDelUsuario(userId).ifPresent(c -> new ArrayList<>(c.getProductos()).forEach(c::removeProducto));
    }

    private Optional<Carrito> buscarDelUsuario(Long userId) {
        return carritoRepository.findFirstByIdUsuarioOrderByIdCarritoDesc(userId);
    }

    private Carrito exigirCarrito(Long userId) {
        return buscarDelUsuario(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El carrito está vacío"));
    }

    private static Optional<CarritoProducto> buscarLinea(Carrito c, Long idProducto) {
        return c.getProductos().stream().filter(x -> x.getIdProducto().equals(idProducto)).findFirst();
    }

    private static CarritoProducto exigirLinea(Carrito c, Long idProducto) {
        return buscarLinea(c, idProducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Producto no encontrado en el carrito"));
    }

    private void validarProductoDisponible(Long idProducto) {
        CatalogoClient.Producto p = catalogoClient.productos(List.of(idProducto)).get(idProducto);
        if (p == null || !Boolean.TRUE.equals(p.activo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no disponible: " + idProducto);
        }
    }
}
