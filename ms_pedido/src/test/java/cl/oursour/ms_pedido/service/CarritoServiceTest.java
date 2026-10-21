package cl.oursour.ms_pedido.service;

import cl.oursour.ms_pedido.client.CatalogoClient;
import cl.oursour.ms_pedido.dto.CarritoResponse;
import cl.oursour.ms_pedido.dto.ProductoCarritoRequest;
import cl.oursour.ms_pedido.model.Carrito;
import cl.oursour.ms_pedido.model.CarritoProducto;
import cl.oursour.ms_pedido.repository.CarritoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private CatalogoClient catalogoClient;

    private CarritoService service;

    @BeforeEach
    void setUp() {
        service = new CarritoService(carritoRepository, catalogoClient);
    }

    @Test
    void sinCarritoDevuelveUnoVacio() {
        when(carritoRepository.findFirstByIdUsuarioOrderByIdCarritoDesc(2L)).thenReturn(Optional.empty());

        CarritoResponse r = service.obtener(2L);

        assertThat(r.idCarrito()).isNull();
        assertThat(r.productos()).isEmpty();
    }

    @Test
    void agregarCreaElCarritoYSumaCantidadSiElProductoYaEsta() {
        disponible(1L, true);
        when(carritoRepository.findFirstByIdUsuarioOrderByIdCarritoDesc(2L)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        CarritoResponse primero = service.agregarProducto(2L, new ProductoCarritoRequest(1L, 2));
        assertThat(primero.idUsuario()).isEqualTo(2L);

        Carrito existente = carrito(1L, 2);
        when(carritoRepository.findFirstByIdUsuarioOrderByIdCarritoDesc(2L)).thenReturn(Optional.of(existente));

        CarritoResponse r = service.agregarProducto(2L, new ProductoCarritoRequest(1L, 3));

        assertThat(r.productos()).singleElement().extracting(CarritoResponse.Linea::cantidad).isEqualTo(5);
    }

    @Test
    void productoNoDisponibleDa400() {
        disponible(1L, false);

        assertThatThrownBy(() -> service.agregarProducto(2L, new ProductoCarritoRequest(1L, 1)))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void cambiarCantidadYEliminarProducto() {
        Carrito c = carrito(1L, 2);
        when(carritoRepository.findFirstByIdUsuarioOrderByIdCarritoDesc(2L)).thenReturn(Optional.of(c));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.cambiarCantidad(2L, 1L, 7).productos().get(0).cantidad()).isEqualTo(7);
        assertThat(service.eliminarProducto(2L, 1L).productos()).isEmpty();
    }

    @Test
    void productoQueNoEstaEnElCarritoDa404() {
        when(carritoRepository.findFirstByIdUsuarioOrderByIdCarritoDesc(2L)).thenReturn(Optional.of(carrito(1L, 2)));

        assertThatThrownBy(() -> service.eliminarProducto(2L, 9L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    private void disponible(Long id, boolean activo) {
        when(catalogoClient.productos(List.of(id))).thenReturn(Map.of(id,
                new CatalogoClient.Producto(id, "Producto", new BigDecimal("7990"), 10, activo)));
    }

    private static Carrito carrito(Long idProducto, int cantidad) {
        Carrito c = new Carrito();
        c.setIdCarrito(3L);
        c.setIdUsuario(2L);
        CarritoProducto p = new CarritoProducto();
        p.setIdProducto(idProducto);
        p.setCantidad(cantidad);
        c.addProducto(p);
        return c;
    }
}
