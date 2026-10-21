package cl.oursour.ms_pedido.client;

import cl.oursour.ms_pedido.security.Autorizacion;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Precios y stock siempre desde ms_catalogo
@Component
public class CatalogoClient {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Producto(Long idProducto, String nombreComercial, BigDecimal precioUnitario,
                           Integer stockSimulado, Boolean activo) {
    }

    public record Item(Long idProducto, Integer cantidad) {
    }

    private final RestClient http;

    public CatalogoClient(@Value("${servicios.catalogo.url}") String url,
                          @Value("${servicios.clave-interna}") String claveInterna) {
        var fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(2));
        fabrica.setReadTimeout(Duration.ofSeconds(5));
        this.http = RestClient.builder().baseUrl(url).requestFactory(fabrica)
                .defaultHeader(Autorizacion.HEADER_CLAVE_INTERNA, claveInterna)
                .build();
    }

    public Map<Long, Producto> productos(Collection<Long> ids) {
        try {
            List<Producto> lista = http.get()
                    .uri(uri -> uri.path("/internal/productos").queryParam("ids", ids.toArray()).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Producto>>() {
                    });
            return (lista != null ? lista : List.<Producto>of()).stream()
                    .collect(Collectors.toMap(Producto::idProducto, Function.identity()));
        } catch (RestClientException ex) {
            throw traducir(ex);
        }
    }

    public void descontarStock(List<Item> items) {
        try {
            http.post().uri("/internal/productos/descontar-stock")
                    .body(Map.of("items", items))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw traducir(ex);
        }
    }

    // 404 y 409 de catálogo se devuelven con su mensaje; cualquier otra falla es "no disponible"
    private static ResponseStatusException traducir(RestClientException ex) {
        if (ex instanceof RestClientResponseException r
                && (r.getStatusCode().value() == 404 || r.getStatusCode().value() == 409)) {
            String mensaje = mensajeDe(r);
            return new ResponseStatusException(HttpStatus.valueOf(r.getStatusCode().value()),
                    mensaje != null ? mensaje : "Error en ms-catalogo");
        }
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Servicio de catálogo no disponible");
    }

    private static String mensajeDe(RestClientResponseException r) {
        try {
            Map<?, ?> cuerpo = r.getResponseBodyAs(Map.class);
            Object mensaje = cuerpo != null ? cuerpo.get("mensaje") : null;
            return mensaje != null ? mensaje.toString() : null;
        } catch (RuntimeException e) {
            return null;
        }
    }
}
