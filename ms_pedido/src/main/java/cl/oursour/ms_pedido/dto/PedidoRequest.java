package cl.oursour.ms_pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

// El comprador sale del JWT; montos y estado los calcula el servidor
public record PedidoRequest(
        @NotNull(message = "idDireccion es obligatorio")
        @Positive(message = "idDireccion debe ser mayor a 0")
        Long idDireccion,

        @NotEmpty(message = "El pedido debe contener al menos un producto")
        List<@Valid ProductoPedidoRequest> productos
) {
}
