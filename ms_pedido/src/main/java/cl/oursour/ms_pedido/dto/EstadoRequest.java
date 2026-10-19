package cl.oursour.ms_pedido.dto;

import cl.oursour.ms_pedido.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;


public record EstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoPedido estado
) {
}
