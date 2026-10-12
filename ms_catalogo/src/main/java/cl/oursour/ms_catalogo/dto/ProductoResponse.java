package cl.oursour.ms_catalogo.dto;

import cl.oursour.ms_catalogo.model.Producto;

public record ProductoResponse(
    long idProducto,
    String nombreComercial,
    String Descripcion,
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
