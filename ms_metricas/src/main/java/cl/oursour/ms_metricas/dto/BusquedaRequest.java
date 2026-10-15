package cl.oursour.ms_metricas.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record BusquedaRequest(
        @NotNull(message = "El usuario es obligatorio")
        Long idUsuario,

        @NotEmpty(message = "Debe venir al menos un ingrediente")
        Set<@NotNull Long> idsIngredientes
) {
}
