package cl.oursour.ms_recetas.dto;

import cl.oursour.ms_recetas.model.Receta;

// Formato que usa ms_metricas en /internal/recetas
public record RecetaResumen(
        Long idReceta,
        String titulo,
        String tipo
) {
    public static RecetaResumen desde(Receta r) {
        return new RecetaResumen(r.getIdReceta(), r.getTitulo(), r.getTipo());
    }
}
