package cl.oursour.ms_recetas.client;

import cl.oursour.ms_recetas.security.Autorizacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

// Registra búsquedas y consultas en ms_metricas. Si métricas no responde, recetas sigue funcionando.
@Component
public class MetricasClient {

    private static final Logger log = LoggerFactory.getLogger(MetricasClient.class);

    private final RestClient http;

    public MetricasClient(@Value("${servicios.metricas.url}") String url,
                          @Value("${servicios.clave-interna}") String claveInterna) {
        var fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(2));
        fabrica.setReadTimeout(Duration.ofSeconds(3));
        this.http = RestClient.builder().baseUrl(url).requestFactory(fabrica)
                .defaultHeader(Autorizacion.HEADER_CLAVE_INTERNA, claveInterna)
                .build();
    }

    public void registrarBusqueda(Long idUsuario, Collection<Long> idsIngredientes) {
        enviar("/internal/busquedas", Map.of("idUsuario", idUsuario, "idsIngredientes", Set.copyOf(idsIngredientes)));
    }

    public void registrarConsulta(Long idUsuario, Long idReceta) {
        enviar("/internal/consultas", Map.of("idUsuario", idUsuario, "idReceta", idReceta));
    }

    private void enviar(String ruta, Map<String, Object> cuerpo) {
        try {
            http.post().uri(ruta).body(cuerpo).retrieve().toBodilessEntity();
        } catch (RestClientException ex) {
            log.warn("No se pudo registrar en ms-metricas ({}): {}", ruta, ex.getMessage());
        }
    }
}
