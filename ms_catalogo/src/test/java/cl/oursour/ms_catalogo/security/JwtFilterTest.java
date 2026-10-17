package cl.oursour.ms_catalogo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtFilterTest {

    private static final String SECRETO = "clave-de-prueba-para-tests-de-al-menos-32-caracteres";
    private static final String CLAVE_INTERNA = "clave-interna-de-prueba";

    private final JwtFilter filtro = new JwtFilter(new JwtService(SECRETO), CLAVE_INTERNA);

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tokenValidoAutenticaYReemplazaLosHeadersXUserDelCliente() throws Exception {
        MockHttpServletRequest pedido = new MockHttpServletRequest("GET", "/api/x");
        pedido.addHeader("Authorization", "Bearer " + token(SECRETO, 7L, "CONSUMIDOR", 60_000));
        pedido.addHeader("X-User-Id", "999");
        pedido.addHeader("X-User-Rol", "ADMIN");
        MockFilterChain cadena = new MockFilterChain();

        filtro.doFilter(pedido, new MockHttpServletResponse(), cadena);

        HttpServletRequest recibido = (HttpServletRequest) cadena.getRequest();
        assertThat(recibido.getHeader("X-User-Id")).isEqualTo("7");
        assertThat(recibido.getHeader("x-user-rol")).isEqualTo("CONSUMIDOR");
        assertThat(Collections.list(recibido.getHeaders("X-User-Id"))).containsExactly("7");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(7L);
    }

    @Test
    void sinTokenNoAutenticaYDescartaLosHeadersXUser() throws Exception {
        MockHttpServletRequest pedido = new MockHttpServletRequest("GET", "/api/x");
        pedido.addHeader("X-User-Id", "999");
        pedido.addHeader("X-User-Rol", "ADMIN");
        MockFilterChain cadena = new MockFilterChain();

        filtro.doFilter(pedido, new MockHttpServletResponse(), cadena);

        HttpServletRequest recibido = (HttpServletRequest) cadena.getRequest();
        assertThat(recibido.getHeader("X-User-Id")).isNull();
        assertThat(recibido.getHeader("X-User-Rol")).isNull();
        assertThat(Collections.list(recibido.getHeaderNames())).doesNotContain("X-User-Id", "X-User-Rol");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void tokenFirmadoConOtraClaveNoAutentica() throws Exception {
        MockHttpServletRequest pedido = new MockHttpServletRequest("GET", "/api/x");
        pedido.addHeader("Authorization",
                "Bearer " + token("otra-clave-distinta-de-al-menos-32-caracteres", 7L, "ADMIN", 60_000));
        MockFilterChain cadena = new MockFilterChain();

        filtro.doFilter(pedido, new MockHttpServletResponse(), cadena);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(((HttpServletRequest) cadena.getRequest()).getHeader("X-User-Rol")).isNull();
    }

    @Test
    void tokenExpiradoNoAutentica() throws Exception {
        MockHttpServletRequest pedido = new MockHttpServletRequest("GET", "/api/x");
        pedido.addHeader("Authorization", "Bearer " + token(SECRETO, 7L, "CONSUMIDOR", -60_000));

        filtro.doFilter(pedido, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void rutaInternaConLaClaveCorrectaQuedaAutenticadaComoInterno() throws Exception {
        MockHttpServletRequest pedido = new MockHttpServletRequest("GET", "/internal/x");
        pedido.addHeader("X-Internal-Key", CLAVE_INTERNA);

        filtro.doFilter(pedido, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting(a -> a.getAuthority()).containsExactly("ROLE_INTERNO");
    }

    @Test
    void rutaInternaConClaveIncorrectaNoAutentica() throws Exception {
        MockHttpServletRequest pedido = new MockHttpServletRequest("GET", "/internal/x");
        pedido.addHeader("X-Internal-Key", "adivinando");

        filtro.doFilter(pedido, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void rutaInternaNoAceptaElJwtDeUnUsuario() throws Exception {
        MockHttpServletRequest pedido = new MockHttpServletRequest("GET", "/internal/x");
        pedido.addHeader("Authorization", "Bearer " + token(SECRETO, 7L, "ADMIN", 60_000));

        filtro.doFilter(pedido, new MockHttpServletResponse(), new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private static String token(String secreto, Long userId, String rol, long expiraEnMs) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject("ana@correo.cl")
                .claim("userId", userId)
                .claim("rol", rol)
                .issuedAt(new Date(ahora.getTime() - 120_000))
                .expiration(new Date(ahora.getTime() + expiraEnMs))
                .signWith(Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();
    }
}
