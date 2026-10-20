package cl.oursour.ms_pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Sin precio: el precio lo pone ms_catalogo
public record ProductoPedidoRequest(
        @NotNull(message = "idProducto es obligatorio en cada producto")
        @Positive(message = "idProducto debe ser mayor a 0")
        Long idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer cantidad
) {
}
