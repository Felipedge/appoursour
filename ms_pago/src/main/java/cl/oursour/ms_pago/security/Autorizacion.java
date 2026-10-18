package cl.oursour.ms_pago.security;

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

    public static boolean esAdmin(String rol) {
        return "ADMIN".equals(rol);
    }

    // Un usuario solo puede ver lo suyo; el ADMIN puede todo
    public static void exigirDuenoOAdmin(Long idDueno, Long userId, String rol) {
        if (!esAdmin(rol) && (idDueno == null || !idDueno.equals(userId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para esta acción");
        }
    }
}

