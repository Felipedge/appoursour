package cl.oursour.ms_catalogo.security;

import cl.oursour.ms_catalogo.controller.InternoController;
import cl.oursour.ms_catalogo.controller.ProductoController;
import cl.oursour.ms_catalogo.service.ProductoService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ProductoController.class, InternoController.class})
@Import({SecurityConfig.class, JwtFilter.class, JwtService.class})
@TestPropertySource(properties = {
        "jwt.secret=" + SeguridadWebTest.SECRETO,
        "servicios.clave-interna=" + SeguridadWebTest.CLAVE_INTERNA})
class SeguridadWebTest {

    static final String SECRETO = "clave-de-prueba-para-tests-de-al-menos-32-caracteres";
    static final String CLAVE_INTERNA = "clave-interna-de-prueba";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void sinTokenResponde401EnFormatoJson() throws Exception {
        mvc.perform(get("/api/productos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Token ausente, inválido o expirado"));
    }

    @Test
    void conTokenValidoResponde200() throws Exception {
        when(productoService.listar()).thenReturn(List.of());

        mvc.perform(get("/api/productos").header("Authorization", "Bearer " + token("CONSUMIDOR")))
                .andExpect(status().isOk());
    }

    @Test
    void consumidorQueSeDeclaraAdminEnElHeaderRecibe403() throws Exception {
        mvc.perform(post("/api/productos")
                        .header("Authorization", "Bearer " + token("CONSUMIDOR"))
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombreComercial":"X","precioUnitario":1000,"stockSimulado":1}"""))
                .andExpect(status().isForbidden());
    }

    @Test
    void rutaInternaSinClaveResponde401() throws Exception {
        mvc.perform(post("/internal/productos/descontar-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"items":[{"idProducto":1,"cantidad":1}]}"""))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Ruta solo para microservicios: falta la clave interna"));
    }

    @Test
    void rutaInternaConElTokenDeUnAdminTambienResponde401() throws Exception {
        mvc.perform(get("/internal/productos").param("ids", "1")
                        .header("Authorization", "Bearer " + token("ADMIN")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rutaInternaConLaClaveResponde200() throws Exception {
        when(productoService.buscarPorIds(List.of(1L))).thenReturn(List.of());

        mvc.perform(get("/internal/productos").param("ids", "1")
                        .header("X-Internal-Key", CLAVE_INTERNA))
                .andExpect(status().isOk());
    }

    private static String token(String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject("ana@correo.cl")
                .claim("userId", 7L)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRETO.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();
    }
}
