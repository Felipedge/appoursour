package cl.oursour.ms_recetas.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String PREFIJO = "Bearer ";
    private static final String RUTAS_INTERNAS = "/internal/";

    private final JwtService jwtService;
    private final byte[] claveInterna;

    public JwtFilter(JwtService jwtService, @Value("${servicios.clave-interna}") String claveInterna) {
        if (claveInterna == null || claveInterna.isBlank()) {
            throw new IllegalStateException("Falta configurar servicios.clave-interna (variable INTERNAL_KEY)");
        }
        this.jwtService = jwtService;
        this.claveInterna = claveInterna.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Map<String, String> usuario = new LinkedHashMap<>();

        if (request.getRequestURI().startsWith(RUTAS_INTERNAS)) {
            if (esClaveInternaValida(request.getHeader(Autorizacion.HEADER_CLAVE_INTERNA))) {
                var auth = new UsernamePasswordAuthenticationToken("servicio-interno", null,
                        List.of(new SimpleGrantedAuthority("ROLE_INTERNO")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(new RequestConUsuario(request, usuario), response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIJO)) {
            String token = header.substring(PREFIJO.length()).trim();
            try {
                Claims claims = jwtService.validar(token);
                Long userId = claims.get(JwtService.CLAIM_USER_ID, Long.class);
                String rol = claims.get(JwtService.CLAIM_ROL, String.class);

                if (userId != null && rol != null) {
                    var auth = new UsernamePasswordAuthenticationToken(userId, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    usuario.put(Autorizacion.HEADER_USUARIO, userId.toString());
                    usuario.put(Autorizacion.HEADER_ROL, rol);
                }
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(new RequestConUsuario(request, usuario), response);
    }

    private boolean esClaveInternaValida(String recibida) {
        return recibida != null
                && MessageDigest.isEqual(claveInterna, recibida.getBytes(StandardCharsets.UTF_8));
    }

    private static class RequestConUsuario extends HttpServletRequestWrapper {

        private final Map<String, String> usuario;

        RequestConUsuario(HttpServletRequest request, Map<String, String> usuario) {
            super(request);
            this.usuario = usuario;
        }

        private static boolean esHeaderUsuario(String nombre) {
            return Autorizacion.HEADER_USUARIO.equalsIgnoreCase(nombre)
                    || Autorizacion.HEADER_ROL.equalsIgnoreCase(nombre);
        }

        private String valorPropio(String nombre) {
            return usuario.entrySet().stream()
                    .filter(e -> e.getKey().equalsIgnoreCase(nombre))
                    .map(e -> e.getValue())
                    .findFirst().orElse(null);
        }

        @Override
        public String getHeader(String nombre) {
            return esHeaderUsuario(nombre) ? valorPropio(nombre) : super.getHeader(nombre);
        }

        @Override
        public Enumeration<String> getHeaders(String nombre) {
            if (esHeaderUsuario(nombre)) {
                String valor = valorPropio(nombre);
                return Collections.enumeration(valor == null ? List.of() : List.of(valor));
            }
            return super.getHeaders(nombre);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> nombres = Collections.list(super.getHeaderNames()).stream()
                    .filter(n -> !esHeaderUsuario(n))
                    .collect(Collectors.toCollection(ArrayList::new));
            nombres.addAll(usuario.keySet());
            return Collections.enumeration(nombres);
        }
    }
}
