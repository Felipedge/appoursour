package cl.oursour.ms_pedido.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductoPedidoRequest(
        @NotNull(message = "idProducto es obligatorio en cada producto")
        @Positive(message = "idProducto debe ser mayor a 0")
        Long idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer cantidad,

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio unitario admite hasta 8 enteros y 2 decimales")
        BigDecimal precioUnitario
) {
}
