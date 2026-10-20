package cl.oursour.ms_recetas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record IngredienteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
        String nombre,

        
        @NotBlank(message = "La categoría es obligatoria")
        String categoria,

        
        @Positive(message = "idProducto debe ser mayor a 0")
        Long idProducto
) {
}
