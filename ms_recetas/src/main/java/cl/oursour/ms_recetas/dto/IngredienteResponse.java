package cl.oursour.ms_recetas.dto;

import cl.oursour.ms_recetas.model.Ingrediente;

public record IngredienteResponse(
        Long idIngrediente,
        String nombre,
        String categoria,
        Long idProducto
) {
    public static IngredienteResponse desde(Ingrediente i) {
        return new IngredienteResponse(i.getIdIngrediente(), i.getNombre(), i.getCategoria(), i.getIdProducto());
    }
}
