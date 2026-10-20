package cl.oursour.ms_pago.client;

import cl.oursour.ms_pago.dto.PedidoRemoto;
import cl.oursour.ms_pago.security.Autorizacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

// Comunicación con ms_pedido: con el token del usuario para leer, con la clave interna para informar el pago
@Component
public class PedidoClient {

    private final RestTemplate restTemplate;
    private final String pedidoUrl;
    private final String claveInterna;

    public PedidoClient(RestTemplate restTemplate,
                        @Value("${servicios.pedido.url}") String pedidoUrl,
                        @Value("${servicios.clave-interna}") String claveInterna) {
        this.restTemplate = restTemplate;
        this.pedidoUrl = pedidoUrl;
        this.claveInterna = claveInterna;
    }

    // ms_pedido responde 403 si el pedido no es del usuario del token
    public PedidoRemoto obtener(Long idPedido, String authorization) {
        try {
            PedidoRemoto pedido = restTemplate.exchange(pedidoUrl + "/api/pedidos/" + idPedido, HttpMethod.GET,
                    new HttpEntity<>(conToken(authorization)), PedidoRemoto.class).getBody();
            if (pedido == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ms-pedido devolvió una respuesta vacía");
            }
            return pedido;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido no existe: " + idPedido);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El pedido no pertenece al usuario");
        } catch (RestClientException e) {
            throw noDisponible();
        }
    }

    public List<PedidoRemoto> misPedidos(String authorization) {
        try {
            List<PedidoRemoto> lista = restTemplate.exchange(pedidoUrl + "/api/pedidos", HttpMethod.GET,
                    new HttpEntity<>(conToken(authorization)),
                    new ParameterizedTypeReference<List<PedidoRemoto>>() {
                    }).getBody();
            return lista != null ? lista : List.of();
        } catch (RestClientException e) {
            throw noDisponible();
        }
    }

    // Aprobado: ms_pedido descuenta stock y pasa a SOLICITADO. Rechazado: pasa a PAGO_RECHAZADO.
    public void registrarResultado(Long idPedido, boolean aprobado) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(Autorizacion.HEADER_CLAVE_INTERNA, claveInterna);
        try {
            restTemplate.postForEntity(pedidoUrl + "/internal/pedidos/" + idPedido + "/resultado-pago",
                    new HttpEntity<>(Map.of("aprobado", aprobado), headers), Void.class);
        } catch (HttpClientErrorException.Conflict e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, mensajeDe(e, "El pedido no se puede pagar"));
        } catch (RestClientException e) {
            throw noDisponible();
        }
    }

    private static HttpHeaders conToken(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorization);
        return headers;
    }

    private static ResponseStatusException noDisponible() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Servicio de pedidos no disponible");
    }

    private static String mensajeDe(RestClientResponseException e, String porDefecto) {
        try {
            Map<?, ?> cuerpo = e.getResponseBodyAs(Map.class);
            Object mensaje = cuerpo != null ? cuerpo.get("mensaje") : null;
            return mensaje != null ? mensaje.toString() : porDefecto;
        } catch (RuntimeException ex) {
            return porDefecto;
        }
    }
}
