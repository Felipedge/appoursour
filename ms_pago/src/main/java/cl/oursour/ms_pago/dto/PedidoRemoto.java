package cl.oursour.ms_pago.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;


@JsonIgnoreProperties(ignoreUnknown = true)
public record PedidoRemoto(
        Long idPedido,
        Long idUsuario,
        BigDecimal montoTotal,
        String estado
) {
}
