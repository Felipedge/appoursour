package cl.oursour.ms_recetas.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

// Ingredientes que el usuario tiene en casa
public record RecomendacionRequest(
        @NotEmpty(message = "Debes indicar al menos un ingrediente")
        List<@NotNull(message = "idIngrediente no puede ser nulo")
             @Positive(message = "idIngrediente debe ser mayor a 0") Long> ingredientes
) {
}
