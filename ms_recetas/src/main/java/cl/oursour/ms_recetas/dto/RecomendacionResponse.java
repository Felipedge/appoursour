package cl.oursour.ms_recetas.dto;

import java.util.List;

public record RecomendacionResponse(
        Long idReceta,
        String titulo,
        String tipo,
        Long idProducto,
        // % de ingredientes de la receta que el usuario ya tiene (0 a 100, un decimal)
        double porcentajeCoincidencia,
        List<IngredienteResponse> ingredientesQueTienes,
        List<IngredienteResponse> ingredientesFaltantes
) {
}
