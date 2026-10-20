package cl.oursour.ms_pedido.dto;

import jakarta.validation.constraints.NotNull;

// Lo envía ms_pago al terminar de procesar un pago
public record ResultadoPagoRequest(
        @NotNull(message = "Debe indicar si el pago fue aprobado")
        Boolean aprobado
) {
}
