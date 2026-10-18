package cl.oursour.ms_pedido.dto;

import cl.oursour.ms_pedido.model.Carrito;
import cl.oursour.ms_pedido.model.CarritoProducto;

import java.time.LocalDateTime;
import java.util.List;

public record CarritoResponse(
        Long idCarrito,
        Long idUsuario,
        LocalDateTime fechaCreacion,
        List<Linea> productos
) {
    public record Linea(Long idDetalle, Long idProducto, int cantidad) {
        static Linea desde(CarritoProducto p) {
            return new Linea(p.getIdDetalle(), p.getIdProducto(), p.getCantidad());
        }
    }

    public static CarritoResponse desde(Carrito c) {
        return new CarritoResponse(c.getIdCarrito(), c.getIdUsuario(), c.getFechaCreacion(),
                c.getProductos().stream().map(Linea::desde).toList());
    }
}
