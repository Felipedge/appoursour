package cl.oursour.ms_catalogo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank(message = "El nombre comercial es obligatorio")
        @Size(max = 150, message = "El nombre comercial no puede tener más de 150 caracteres")
        String nombreComercial,

        @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
        String descripcion,

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
        @Digits(integer = 8, fraction = 2, message = "El precio unitario admite hasta 8 enteros y 2 decimales")
        BigDecimal precioUnitario,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stockSimulado
) {
}

