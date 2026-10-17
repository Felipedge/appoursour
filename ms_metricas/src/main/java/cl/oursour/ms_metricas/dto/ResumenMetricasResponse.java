package cl.oursour.ms_metricas.dto;

import java.math.BigDecimal;

public record ResumenMetricasResponse(
        long totalBusquedas,
        long totalConsultasReceta,
        long totalPedidosPagados,
        BigDecimal ventasTotales
) {
}
