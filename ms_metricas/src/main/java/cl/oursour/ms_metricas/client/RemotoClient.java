package cl.oursour.ms_metricas.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import cl.oursour.ms_metricas.dto.ConteoId;
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
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RemotoClient {

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Ingrediente(Long idIngrediente, String nombre) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Receta(Long idReceta, String titulo) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Producto(Long idProducto, String nombreComercial) {
    }

    public record Ventas(long pedidosPagados, BigDecimal ventasTotales) {
    }

    private final RestClient recetas;
    private final RestClient catalogo;
    private final RestClient pedido;

    public RemotoClient(@Value("${servicios.recetas.url}") String urlRecetas,
                        @Value("${servicios.catalogo.url}") String urlCatalogo,
                        @Value("${servicios.pedido.url}") String urlPedido) {
        this.recetas = crear(urlRecetas);
        this.catalogo = crear(urlCatalogo);
        this.pedido = crear(urlPedido);
    }

    public Map<Long, String> nombresIngredientes(Collection<Long> ids) {
        List<Ingrediente> lista = obtenerLista(recetas, "ms-recetas", "/internal/ingredientes", ids,
                new ParameterizedTypeReference<>() {
                });
        return lista.stream().collect(Collectors.toMap(i -> i.idIngrediente(), i -> i.nombre()));
    }

    public Map<Long, String> titulosRecetas(Collection<Long> ids) {
        List<Receta> lista = obtenerLista(recetas, "ms-recetas", "/internal/recetas", ids,
                new ParameterizedTypeReference<>() {
                });
        return lista.stream().collect(Collectors.toMap(r -> r.idReceta(), r -> r.titulo()));
    }

    public Map<Long, String> nombresProductos(Collection<Long> ids) {
        List<Producto> lista = obtenerLista(catalogo, "ms-catalogo", "/internal/productos", ids,
                new ParameterizedTypeReference<>() {
                });
        return lista.stream().collect(Collectors.toMap(p -> p.idProducto(), p -> p.nombreComercial()));
    }

    public Ventas ventas() {
        try {
            return pedido.get().uri("/internal/metricas/ventas").retrieve().body(Ventas.class);
        } catch (RestClientException ex) {
            throw ErroresRemotos.traducir("ms-pedido", ex);
        }
    }

    public List<ConteoId> productosMasVendidos(int limite) {
        try {
            List<ConteoId> lista = pedido.get()
                    .uri(uri -> uri.path("/internal/metricas/productos-mas-vendidos").queryParam("limite", limite).build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ConteoId>>() {
                    });
            return lista != null ? lista : List.of();
        } catch (RestClientException ex) {
            throw ErroresRemotos.traducir("ms-pedido", ex);
        }
    }

    private static <T> List<T> obtenerLista(RestClient http, String servicio, String ruta, Collection<Long> ids,
                                            ParameterizedTypeReference<List<T>> tipo) {
        if (ids.isEmpty()) {
            return List.of();
        }
        try {
            List<T> lista = http.get()
                    .uri(uri -> uri.path(ruta).queryParam("ids", ids.toArray()).build())
                    .retrieve()
                    .body(tipo);
            return lista != null ? lista : List.of();
        } catch (RestClientException ex) {
            throw ErroresRemotos.traducir(servicio, ex);
        }
    }

    private static RestClient crear(String url) {
        var fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(2));
        fabrica.setReadTimeout(Duration.ofSeconds(5));
        return RestClient.builder().baseUrl(url).requestFactory(fabrica).build();
    }
}
