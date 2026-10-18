package cl.oursour.ms_pedido.dto;

import cl.oursour.ms_pedido.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

// Solo acepta PENDIENTE_PAGO, SOLICITADO, EN_PREPARACION o PAGO_RECHAZADO
public record EstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoPedido estado
) {
}
