package cl.oursour.ms_recetas.dto;

import java.math.BigDecimal;
import java.util.List;

// Detalle de una receta con nombres de ingredientes y productos Our Sour sugeridos
public record RecetaDetalleResponse(
        Long idReceta,
        String titulo,
        String tipo,
        String pasosPreparacion,
        Long idProducto,
        List<Ingrediente> ingredientes,
        List<ProductoSugerido> productosSugeridos
) {
    public record Ingrediente(Long idIngrediente, String nombre, String categoria, String dosificacion) {
    }

    public record ProductoSugerido(Long idProducto, String nombreComercial, BigDecimal precioUnitario) {
    }
}
