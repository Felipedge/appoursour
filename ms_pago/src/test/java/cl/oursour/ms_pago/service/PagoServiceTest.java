package cl.oursour.ms_pago.service;

import cl.oursour.ms_pago.client.PedidoClient;
import cl.oursour.ms_pago.dto.PagoRequest;
import cl.oursour.ms_pago.dto.PagoResponse;
import cl.oursour.ms_pago.dto.PedidoRemoto;
import cl.oursour.ms_pago.model.EstadoPago;
import cl.oursour.ms_pago.model.Pago;
import cl.oursour.ms_pago.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    private static final String TOKEN = "Bearer abc";

    @Mock
    private PagoRepository repository;

    @Mock
    private PedidoClient pedidoClient;

    private PagoService service;

    @BeforeEach
    void setUp() {
        service = new PagoService(repository, pedidoClient);
    }

    @Test
    void tarjetaNormalApruebaYElMontoSaleDelPedido() {
        pedidoPendiente();
        when(repository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoResponse r = service.procesarPago(new PagoRequest(6L, "DEBITO", "4111111111111111"), 2L, TOKEN);

        assertThat(r.estado()).isEqualTo(EstadoPago.APROBADO);
        assertThat(r.monto()).isEqualByComparingTo("19970");
        verify(pedidoClient).registrarResultado(6L, true);
    }

    @Test
    void tarjetaTerminadaEn0000SeRechaza() {
        pedidoPendiente();
        when(repository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoResponse r = service.procesarPago(new PagoRequest(6L, "CREDITO", "4111111111110000"), 2L, TOKEN);

        assertThat(r.estado()).isEqualTo(EstadoPago.RECHAZADO);
        verify(pedidoClient).registrarResultado(6L, false);
    }

    @Test
    void pedidoDeOtroUsuarioDa403() {
        pedidoPendiente();

        assertThatThrownBy(() -> service.procesarPago(new PagoRequest(6L, "DEBITO", "4111111111111111"), 9L, TOKEN))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
        verify(pedidoClient, never()).registrarResultado(anyLong(), anyBoolean());
    }

    @Test
    void pedidoYaPagadoDa409() {
        when(pedidoClient.obtener(6L, TOKEN))
                .thenReturn(new PedidoRemoto(6L, 2L, new BigDecimal("19970"), "SOLICITADO"));

        assertThatThrownBy(() -> service.procesarPago(new PagoRequest(6L, "DEBITO", "4111111111111111"), 2L, TOKEN))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void sinStockNoSeRegistraElPago() {
        pedidoPendiente();
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente"))
                .when(pedidoClient).registrarResultado(6L, true);

        assertThatThrownBy(() -> service.procesarPago(new PagoRequest(6L, "DEBITO", "4111111111111111"), 2L, TOKEN))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(repository, never()).save(any());
    }

    @Test
    void consumidorSoloVeLosPagosDeSusPedidos() {
        when(pedidoClient.misPedidos(TOKEN)).thenReturn(List.of(
                new PedidoRemoto(6L, 2L, BigDecimal.TEN, "SOLICITADO"),
                new PedidoRemoto(7L, 2L, BigDecimal.TEN, "PENDIENTE_PAGO")));
        when(repository.findByIdPedidoIn(List.of(6L, 7L))).thenReturn(List.of(pago(1L, 6L)));

        assertThat(service.listarPagos("CONSUMIDOR", TOKEN)).extracting(PagoResponse::idPago).containsExactly(1L);
        verify(repository, never()).findAll();
    }

    @Test
    void verPagoAjenoDa403() {
        when(repository.findById(1L)).thenReturn(Optional.of(pago(1L, 6L)));
        when(pedidoClient.obtener(6L, TOKEN))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "El pedido no pertenece al usuario"));

        assertThatThrownBy(() -> service.buscarPorId(1L, "CONSUMIDOR", TOKEN))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
    }

    @Test
    void adminVeCualquierPagoSinConsultarPedido() {
        when(repository.findById(1L)).thenReturn(Optional.of(pago(1L, 6L)));

        assertThat(service.buscarPorId(1L, "ADMIN", TOKEN).idPedido()).isEqualTo(6L);
        verify(pedidoClient, never()).obtener(anyLong(), any());
    }

    private void pedidoPendiente() {
        when(pedidoClient.obtener(6L, TOKEN))
                .thenReturn(new PedidoRemoto(6L, 2L, new BigDecimal("19970"), "PENDIENTE_PAGO"));
    }

    private static Pago pago(Long id, Long idPedido) {
        Pago p = new Pago();
        p.setIdPago(id);
        p.setIdPedido(idPedido);
        p.setMonto(BigDecimal.TEN);
        p.setEstado(EstadoPago.APROBADO);
        return p;
    }
}
