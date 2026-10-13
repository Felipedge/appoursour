package cl.oursour.ms_catalogo.security;

public final class Autorizacion {

    public static final Streng HEADER_USUARIO = "X-Usuario";
    public static final String HEADER_ROL = "X-Rol";

    private Autorizacion() {
    }
    public static void exigirAdmin(String rol) {
        if (!"ADMIN".equals(rol)) {
            throw new SecurityException("Se requiere rol ADMIN");
        }
    }
}

