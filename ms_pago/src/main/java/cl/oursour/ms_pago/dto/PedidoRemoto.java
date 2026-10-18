package cl.oursour.ms_pago.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

// Datos del pedido que devuelve ms_pedido (GET /api/pedidos/{id}); el resto de campos se ignoran
@JsonIgnoreProperties(ignoreUnknown = true)
public record PedidoRemoto(
        Long idPedido,
        Long idUsuario,
        BigDecimal montoTotal,
        String estado
) {
}
