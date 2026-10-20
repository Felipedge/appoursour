package cl.oursour.ms_pedido.dto;

import cl.oursour.ms_pedido.model.EstadoPedido;
import cl.oursour.ms_pedido.model.Pedido;
import cl.oursour.ms_pedido.model.PedidoProducto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long idPedido,
        Long idUsuario,
        Long idDireccion,
        BigDecimal subtotal,
        BigDecimal costoDespacho,
        BigDecimal montoTotal,
        EstadoPedido estado,
        LocalDateTime fechaCreacion,
        List<Linea> productos
) {
    public record Linea(Long idProducto, int cantidad, BigDecimal precioUnitario) {
        static Linea desde(PedidoProducto p) {
            return new Linea(p.getIdProducto(), p.getCantidad(), p.getPrecioUnitario());
        }
    }

    public static PedidoResponse desde(Pedido p) {
        return new PedidoResponse(p.getIdPedido(), p.getIdUsuario(), p.getIdDireccion(), p.getSubtotal(),
                p.getCostoDespacho(), p.getMontoTotal(), p.getEstado(), p.getFechaCreacion(),
                p.getProductos().stream().map(Linea::desde).toList());
    }
}
