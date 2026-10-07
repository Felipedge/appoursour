package cl.oursour.ms_usuarios.dto;

import cl.oursour.ms_usuarios.model.Usuario;
import java.time.LocalDateTime;

public record AuthResponse(
        String token,
        String tipo,
        DatosUsuario usuario
) {
    public AuthResponse(String token, Usuario usuario) {
        this(token, "Bearer", DatosUsuario.desde(usuario));
    }

    public record DatosUsuario(
            Long idUsuario,
            String nombre,
            String email,
            String rol,
            LocalDateTime fechaRegistro
    ) {
        public static DatosUsuario desde(Usuario u) {
            return new DatosUsuario(u.getIdUsuario(), u.getNombre(), u.getEmail(),
                    u.getRol().name(), u.getFechaRegistro());
        }
    }
}
