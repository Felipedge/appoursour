package cl.oursour.ms_pedido.dto;

import java.math.BigDecimal;


public record VentasResponse(
        long pedidosPagados,
        BigDecimal ventasTotales
) {
}
