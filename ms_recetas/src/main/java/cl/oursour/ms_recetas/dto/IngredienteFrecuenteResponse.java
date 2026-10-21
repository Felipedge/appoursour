package cl.oursour.ms_recetas.dto;

public record IngredienteFrecuenteResponse(
        Long idIngrediente,
        String nombre,
        String categoria,
        long cantidadRecetas
) {
}
