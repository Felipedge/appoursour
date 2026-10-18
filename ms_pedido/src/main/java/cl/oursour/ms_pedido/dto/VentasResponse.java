package cl.oursour.ms_pedido.dto;

import java.math.BigDecimal;

// Mismo formato que espera ms_metricas (RemotoClient.Ventas)
public record VentasResponse(
        long pedidosPagados,
        BigDecimal ventasTotales
) {
}
