package cl.oursour.ms_pedido.dto;

// Mismo formato que espera ms_metricas: id del producto y unidades vendidas
public record ConteoId(
        Long id,
        Long cantidad
) {
}
