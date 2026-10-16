package cl.oursour.ms_catalogo.dto;

import java.math.BigDecimal;

import cl.oursour.ms_catalogo.model.Producto;

public record ProductoResponse(
    Long idProducto,
    String nombreComercial,
    String descripcion,
    BigDecimal precioUnitario,
    Integer stockSimulado,
    Boolean activo
) {

    public static ProductoResponse desde(Producto p) {
        return new ProductoResponse(
            p.getIdProducto(),
            p.getNombreComercial(),
            p.getDescripcion(),
            p.getPrecioUnitario(),
            p.getStockSimulado(),
            p.getActivo()
        );
    }


}
