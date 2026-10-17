package cl.oursour.ms_recetas.dto;

import cl.oursour.ms_recetas.model.Receta;

// Respuesta de /internal/recetas: sin los detalles (lazy), para no cargarlos fuera de la sesión
public record RecetaResumen(Long idReceta, String titulo, String tipo) {

    public static RecetaResumen desde(Receta r) {
        return new RecetaResumen(r.getIdReceta(), r.getTitulo(), r.getTipo());
    }
}
