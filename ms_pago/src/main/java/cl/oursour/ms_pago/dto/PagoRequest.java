package cl.oursour.ms_pago.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

// Ni el monto ni el estado vienen del cliente: el monto sale del pedido y el estado lo decide el servidor
public record PagoRequest(
        @NotNull(message = "idPedido es obligatorio")
        @Positive(message = "idPedido debe ser mayor a 0")
        Long idPedido,

        @NotBlank(message = "El método de pago es obligatorio")
        @Pattern(regexp = "DEBITO|CREDITO|TRANSFERENCIA",
                message = "El método debe ser DEBITO, CREDITO o TRANSFERENCIA")
        String metodo,

        // Pasarela simulada: true para probar el flujo de pago rechazado
        Boolean simularRechazo
) {
}
