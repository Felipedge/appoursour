package cl.oursour.ms_catalogo.service;

import cl.oursour.ms_catalogo.dto.DescuentoStockRequest;
import cl.oursour.ms_catalogo.dto.DescuentoStockRequest.Item;
import cl.oursour.ms_catalogo.dto.ProductoRequest;
import cl.oursour.ms_catalogo.model.Producto;
import cl.oursour.ms_catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    private ProductoService service;

    @BeforeEach
    void setUp() {
        service = new ProductoService(productoRepository);
    }

    @Test
    void descontarStockRestaLasCantidades() {
        Producto mezcla = producto(1L, "Our Sour Clásico", 10);
        Producto azucar = producto(5L, "Azúcar flor", 4);
        when(productoRepository.findParaActualizar(anyCollection())).thenReturn(List.of(mezcla, azucar));

        service.descontarStock(new DescuentoStockRequest(List.of(new Item(1L, 3), new Item(5L, 4))));

        assertThat(mezcla.getStockSimulado()).isEqualTo(7);
        assertThat(azucar.getStockSimulado()).isZero();
    }

    @Test
    void siUnProductoNoAlcanzaNoSeDescuentaNinguno() {
        Producto mezcla = producto(1L, "Our Sour Clásico", 10);
        Producto azucar = producto(5L, "Azúcar flor", 1);
        when(productoRepository.findParaActualizar(anyCollection())).thenReturn(List.of(mezcla, azucar));

        assertThatThrownBy(() -> service.descontarStock(
                new DescuentoStockRequest(List.of(new Item(1L, 3), new Item(5L, 2)))))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT))
                .hasMessageContaining("Azúcar flor");
        assertThat(mezcla.getStockSimulado()).isEqualTo(10);
        assertThat(azucar.getStockSimulado()).isEqualTo(1);
    }

    @Test
    void productoRepetidoEnLaSolicitudSumaSusCantidades() {
        Producto mezcla = producto(1L, "Our Sour Clásico", 5);
        when(productoRepository.findParaActualizar(anyCollection())).thenReturn(List.of(mezcla));

        // 3 + 3 = 6 > 5 disponibles
        assertThatThrownBy(() -> service.descontarStock(
                new DescuentoStockRequest(List.of(new Item(1L, 3), new Item(1L, 3)))))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void eliminarEsBorradoLogico() {
        Producto mezcla = producto(1L, "Our Sour Clásico", 5);
        when(productoRepository.findByIdProductoAndActivoTrue(1L)).thenReturn(Optional.of(mezcla));

        service.eliminar(1L);

        assertThat(mezcla.getActivo()).isFalse();
    }

    @Test
    void actualizarProductoDadoDeBajaResponde404() {
        when(productoRepository.findByIdProductoAndActivoTrue(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(1L,
                new ProductoRequest("Nuevo", null, new BigDecimal("1000"), 5)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.NOT_FOUND));
    }

    private static Producto producto(Long id, String nombre, int stock) {
        Producto p = new Producto(nombre, null, new BigDecimal("1000"), stock);
        ReflectionTestUtils.setField(p, "idProducto", id);
        return p;
    }
}
