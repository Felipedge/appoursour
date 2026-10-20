package cl.oursour.ms_recetas.dto;

import cl.oursour.ms_recetas.model.DetalleReceta;
import cl.oursour.ms_recetas.model.Receta;

import java.util.List;

public record RecetaResponse(
        Long idReceta,
        String titulo,
        String tipo,
        String pasosPreparacion,
        Long idProducto,
        List<Detalle> detalles
) {
    public record Detalle(Long idIngrediente, String dosificacion) {
        static Detalle desde(DetalleReceta d) {
            return new Detalle(d.getIdIngrediente(), d.getDosificacion());
        }
    }

    public static RecetaResponse desde(Receta r) {
        return new RecetaResponse(r.getIdReceta(), r.getTitulo(), r.getTipo(), r.getPasosPreparacion(),
                r.getIdProducto(), r.getDetalles().stream().map(Detalle::desde).toList());
    }
}
