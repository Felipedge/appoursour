package cl.oursour.ms_recetas.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecetaRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título no puede tener más de 150 caracteres")
        String titulo,

        @NotBlank(message = "El tipo es obligatorio")
        String tipo,

        @NotBlank(message = "Los pasos de preparación son obligatorios")
        String pasosPreparacion,

        // Producto Our Sour asociado (opcional)
        @Positive(message = "idProducto debe ser mayor a 0")
        Long idProducto,

        @NotEmpty(message = "La receta debe contener al menos un ingrediente")
        List<@Valid DetalleRequest> detalles
) {
}
