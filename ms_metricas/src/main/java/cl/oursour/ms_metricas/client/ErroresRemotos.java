package cl.oursour.ms_metricas.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

public final class ErroresRemotos {

    @JsonIgnoreProperties(ignoreUnknown = true)
    record CuerpoError(String mensaje) {
    }

    private ErroresRemotos() {
    }

   
    public static ResponseStatusException traducir(String servicio, RestClientException ex) {
        if (ex instanceof RestClientResponseException respuesta && respuesta.getStatusCode().is4xxClientError()) {
            HttpStatusCode status = respuesta.getStatusCode();
            return new ResponseStatusException(status, leerMensaje(respuesta, servicio));
        }
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, servicio + " no está disponible");
    }

    private static String leerMensaje(RestClientResponseException respuesta, String servicio) {
        try {
            CuerpoError cuerpo = respuesta.getResponseBodyAs(CuerpoError.class);
            if (cuerpo != null && cuerpo.mensaje() != null) {
                return cuerpo.mensaje();
            }
        } catch (RuntimeException ignorada) {
            
        }
        return "Error en " + servicio;
    }
}
