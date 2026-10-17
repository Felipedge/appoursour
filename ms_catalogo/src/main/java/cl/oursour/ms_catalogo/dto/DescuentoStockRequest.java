package cl.oursour.ms_catalogo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DescuentoStockRequest(
        @NotEmpty(message = "Debe venir al menos un ítem")
        List<@Valid Item> items
) {
    public record Item(
            @NotNull(message = "El producto es obligatorio")
            Long idProducto,

            @NotNull(message = "La cantidad es obligatoria")
            @Min(value = 1, message = "La cantidad debe ser al menos 1")
            Integer cantidad
    ) {
    }
}
