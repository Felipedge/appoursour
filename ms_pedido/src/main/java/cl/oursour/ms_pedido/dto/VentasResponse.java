package cl.oursour.ms_pedido.dto;

import java.math.BigDecimal;

// Respuesta de /internal/metricas/ventas (la consume ms_metricas)
public record VentasResponse(long pedidosPagados, BigDecimal ventasTotales) {
}
