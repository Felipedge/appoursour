package cl.oursour.ms_pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

// Si no se envían productos, el pedido se arma con el carrito del usuario
public record PedidoRequest(
        @NotNull(message = "idDireccion es obligatorio")
        @Positive(message = "idDireccion debe ser mayor a 0")
        Long idDireccion,

        List<@Valid ProductoPedidoRequest> productos
) {
}
