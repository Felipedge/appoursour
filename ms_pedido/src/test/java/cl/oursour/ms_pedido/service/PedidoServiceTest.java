package cl.oursour.ms_pedido.service;

import cl.oursour.ms_pedido.client.CatalogoClient;
import cl.oursour.ms_pedido.dto.PedidoRequest;
import cl.oursour.ms_pedido.dto.PedidoResponse;
import cl.oursour.ms_pedido.dto.ProductoPedidoRequest;
import cl.oursour.ms_pedido.model.CarritoProducto;
import cl.oursour.ms_pedido.model.EstadoPedido;
import cl.oursour.ms_pedido.model.Pedido;
import cl.oursour.ms_pedido.model.PedidoProducto;
import cl.oursour.ms_pedido.repository.PedidoRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DireccionService direccionService;

    @Mock
    private CarritoService carritoService;

    @Mock
    private CatalogoClient catalogoClient;

    private PedidoService service;

    @BeforeEach
    void setUp() {
        service = new PedidoService(pedidoRepository, direccionService, carritoService, catalogoClient);
    }

    @Test
    void elPrecioSaleDeCatalogoYSeCalculaElDespacho() {
        when(catalogoClient.productos(any())).thenReturn(Map.of(1L, producto(1L, "7990", 50, true)));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponse r = service.crearPedido(2L, new PedidoRequest(5L,
                List.of(new ProductoPedidoRequest(1L, 2))));

        assertThat(r.productos().get(0).precioUnitario()).isEqualByComparingTo("7990");
        assertThat(r.subtotal()).isEqualByComparingTo("15980");
        assertThat(r.costoDespacho()).isEqualByComparingTo("3990");
        assertThat(r.montoTotal()).isEqualByComparingTo("19970");
        assertThat(r.estado()).isEqualTo(EstadoPedido.PENDIENTE_PAGO);
        verify(direccionService).buscarDelUsuario(5L, 2L);
    }

    @Test
    void despachoGratisDesde50000() {
        when(catalogoClient.productos(any())).thenReturn(Map.of(1L, producto(1L, "10000", 50, true)));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponse r = service.crearPedido(2L, new PedidoRequest(5L, List.of(new ProductoPedidoRequest(1L, 5))));

        assertThat(r.costoDespacho()).isEqualByComparingTo("0");
    }

    @Test
    void sinStockSuficienteDa409() {
        when(catalogoClient.productos(any())).thenReturn(Map.of(1L, producto(1L, "7990", 1, true)));

        assertThatThrownBy(() -> service.crearPedido(2L,
                new PedidoRequest(5L, List.of(new ProductoPedidoRequest(1L, 2)))))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void productoInactivoOInexistenteDa400() {
        when(catalogoClient.productos(any())).thenReturn(Map.of(1L, producto(1L, "7990", 50, false)));

        assertThatThrownBy(() -> service.crearPedido(2L,
                new PedidoRequest(5L, List.of(new ProductoPedidoRequest(1L, 1), new ProductoPedidoRequest(9L, 1)))))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void sinProductosSeUsaElCarritoYLuegoSeVacia() {
        CarritoProducto c = new CarritoProducto();
        c.setIdProducto(1L);
        c.setCantidad(3);
        when(carritoService.productosDelUsuario(2L)).thenReturn(List.of(c));
        when(catalogoClient.productos(any())).thenReturn(Map.of(1L, producto(1L, "7000", 50, true)));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponse r = service.crearPedido(2L, new PedidoRequest(5L, null));

        assertThat(r.subtotal()).isEqualByComparingTo("21000");
        verify(carritoService).vaciarSiExiste(2L);
    }

    @Test
    void carritoVacioDa400() {
        when(carritoService.productosDelUsuario(2L)).thenReturn(List.of());

        assertThatThrownBy(() -> service.crearPedido(2L, new PedidoRequest(5L, List.of())))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void pagoAprobadoDescuentaStockYPasaASolicitado() {
        Pedido p = pedido(EstadoPedido.PENDIENTE_PAGO);
        when(pedidoRepository.findById(6L)).thenReturn(Optional.of(p));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponse r = service.registrarResultadoPago(6L, true);

        assertThat(r.estado()).isEqualTo(EstadoPedido.SOLICITADO);
        verify(catalogoClient).descontarStock(List.of(new CatalogoClient.Item(1L, 2)));
    }

    @Test
    void pagoRechazadoNoDescuentaStock() {
        when(pedidoRepository.findById(6L)).thenReturn(Optional.of(pedido(EstadoPedido.PENDIENTE_PAGO)));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        PedidoResponse r = service.registrarResultadoPago(6L, false);

        assertThat(r.estado()).isEqualTo(EstadoPedido.PAGO_RECHAZADO);
        verify(catalogoClient, never()).descontarStock(any());
    }

    @Test
    void pedidoYaPagadoNoAceptaOtroPago() {
        when(pedidoRepository.findById(6L)).thenReturn(Optional.of(pedido(EstadoPedido.SOLICITADO)));

        assertThatThrownBy(() -> service.registrarResultadoPago(6L, true))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void consumidorNoVePedidoAjeno() {
        when(pedidoRepository.findById(6L)).thenReturn(Optional.of(pedido(EstadoPedido.PENDIENTE_PAGO)));

        assertThatThrownBy(() -> service.obtener(6L, 99L, "CONSUMIDOR"))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    private static CatalogoClient.Producto producto(Long id, String precio, int stock, boolean activo) {
        return new CatalogoClient.Producto(id, "Producto " + id, new BigDecimal(precio), stock, activo);
    }

    private static Pedido pedido(EstadoPedido estado) {
        Pedido p = new Pedido();
        p.setIdPedido(6L);
        p.setIdUsuario(2L);
        p.setEstado(estado);
        PedidoProducto d = new PedidoProducto();
        d.setIdProducto(1L);
        d.setCantidad(2);
        d.setPrecioUnitario(new BigDecimal("7990"));
        p.addProducto(d);
        return p;
    }
}
