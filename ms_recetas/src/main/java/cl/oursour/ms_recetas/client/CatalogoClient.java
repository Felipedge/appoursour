package cl.oursour.ms_recetas.client;

import cl.oursour.ms_recetas.security.Autorizacion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

// Consulta productos en ms_catalogo para sugerirlos en el detalle de una receta
@Component
public class CatalogoClient {

    private static final Logger log = LoggerFactory.getLogger(CatalogoClient.class);

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Producto(Long idProducto, String nombreComercial, BigDecimal precioUnitario, Boolean activo) {
    }

    private final RestClient http;

    public CatalogoClient(@Value("${servicios.catalogo.url}") String url,
                          @Value("${servicios.clave-interna}") String claveInterna) {
        var fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(2));
        fabrica.setReadTimeout(Duration.ofSeconds(3));
        this.http = RestClient.builder().baseUrl(url).requestFactory(fabrica)
                .defaultHeader(Autorizacion.HEADER_CLAVE_INTERNA, claveInterna)
                .build();
    }

    // Si catálogo no responde, la receta se muestra igual, sin productos sugeridos
    public List<Producto> productos(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        try {
            List<Producto> lista = http.get()
                    .uri(uri -> uri.path("/internal/productos").queryParam("ids", ids.toArray()).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Producto>>() {
                    });
            return lista != null ? lista : List.of();
        } catch (RestClientException ex) {
            log.warn("No se pudieron obtener productos de ms-catalogo: {}", ex.getMessage());
            return List.of();
        }
    }
}
