package cl.oursour.ms_metricas.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class Autorizacion {

    public static final String HEADER_USUARIO = "X-User-Id";
    public static final String HEADER_ROL = "X-User-Rol";
    public static final String HEADER_CLAVE_INTERNA = "X-Internal-Key";

    private Autorizacion() {
    }

    public static void exigirAdmin(String rol) {
        if (!"ADMIN".equals(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para esta acción");
        }
    }
}
