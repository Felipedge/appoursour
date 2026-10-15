package cl.oursour.ms_metricas.dto;

import jakarta.validation.constraints.NotNull;

public record ConsultaRequest(
        @NotNull(message = "El usuario es obligatorio")
        Long idUsuario,

        @NotNull(message = "La receta es obligatoria")
        Long idReceta
) {
}
