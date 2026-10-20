package cl.oursour.ms_pago.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;


public record PagoRequest(
        @NotNull(message = "idPedido es obligatorio")
        @Positive(message = "idPedido debe ser mayor a 0")
        Long idPedido,

        @NotBlank(message = "El método de pago es obligatorio")
        @Pattern(regexp = "DEBITO|CREDITO", message = "El método debe ser DEBITO o CREDITO")
        String metodo,

        @NotBlank(message = "El número de tarjeta es obligatorio")
        @Pattern(regexp = "\\d{13,19}", message = "El número de tarjeta debe tener entre 13 y 19 dígitos")
        String numeroTarjeta
) {
}
