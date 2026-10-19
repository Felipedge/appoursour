package cl.oursour.ms_recetas.dto;

import java.util.List;

public record RecomendacionResponse(
        Long idReceta,
        String titulo,
        String tipo,
        Long idProducto,
        double porcentajeCoincidencia,
        List<IngredienteResponse> ingredientesQueTienes,
        List<IngredienteResponse> ingredientesFaltantes
) {
}
