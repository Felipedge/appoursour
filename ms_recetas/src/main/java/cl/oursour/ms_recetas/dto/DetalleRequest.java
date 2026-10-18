package cl.oursour.ms_recetas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DetalleRequest(
        @NotNull(message = "idIngrediente es obligatorio en cada detalle")
        @Positive(message = "idIngrediente debe ser mayor a 0")
        Long idIngrediente,

        @NotBlank(message = "dosificacion es obligatoria en cada detalle")
        @Size(max = 100, message = "La dosificación no puede tener más de 100 caracteres")
        String dosificacion
) {
}
