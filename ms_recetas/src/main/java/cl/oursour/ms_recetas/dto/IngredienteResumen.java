package cl.oursour.ms_recetas.dto;

import cl.oursour.ms_recetas.model.Ingrediente;

// Respuesta de /internal/ingredientes: solo lo que necesitan los otros microservicios
public record IngredienteResumen(Long idIngrediente, String nombre, String categoria) {

    public static IngredienteResumen desde(Ingrediente i) {
        return new IngredienteResumen(i.getIdIngrediente(), i.getNombre(), i.getCategoria());
    }
}
