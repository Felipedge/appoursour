package cl.oursour.ms_pedido.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record DireccionRequest(
        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 150, message = "La calle no puede tener más de 150 caracteres")
        String calle,

        @NotBlank(message = "El número es obligatorio")
        @Size(max = 20, message = "El número no puede tener más de 20 caracteres")
        String numero,

        @NotBlank(message = "La comuna es obligatoria")
        @Size(max = 100, message = "La comuna no puede tener más de 100 caracteres")
        String comuna,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 100, message = "La ciudad no puede tener más de 100 caracteres")
        String ciudad,

        @Size(max = 255, message = "La referencia no puede tener más de 255 caracteres")
        String referencia
) {
}
