package cl.oursour.ms_pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

// El dueño del carrito sale del JWT, no del cuerpo
public record CarritoRequest(
        @NotEmpty(message = "El carrito debe contener al menos un producto")
        List<@Valid ProductoCarritoRequest> productos
) {
}
