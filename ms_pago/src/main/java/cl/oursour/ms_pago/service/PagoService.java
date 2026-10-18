package cl.oursour.ms_pago.service;

import cl.oursour.ms_pago.dto.PagoRequest;
import cl.oursour.ms_pago.dto.PagoResponse;
import cl.oursour.ms_pago.dto.PedidoRemoto;
import cl.oursour.ms_pago.model.EstadoPago;
import cl.oursour.ms_pago.model.Pago;
import cl.oursour.ms_pago.repository.PagoRepository;
import cl.oursour.ms_pago.security.Autorizacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class PagoService {

    // Estados del pedido en los que se puede pagar (PAGO_RECHAZADO permite reintentar)
    private static final Set<String> ESTADOS_PAGABLES = Set.of("PENDIENTE_PAGO", "PAGO_RECHAZADO");

    private final PagoRepository repository;

    private final RestTemplate restTemplate;

    private final String pedidoUrl;

    PagoService(PagoRepository repository, RestTemplate restTemplate,
                @Value("${servicios.pedido.url}") String pedidoUrl) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.pedidoUrl = pedidoUrl;
    }

    // El ADMIN ve todos los pagos; un consumidor solo los suyos
    public List<PagoResponse> listarPagos(Long userId, String rol) {
        List<Pago> pagos = Autorizacion.esAdmin(rol)
                ? repository.findAll()
                : repository.findByIdUsuario(userId);
        return pagos.stream().map(PagoResponse::desde).toList();
    }

    public PagoResponse buscarPorId(Long id, Long userId, String rol) {
        Pago pago = buscarPago(id);
        Autorizacion.exigirDuenoOAdmin(pago.getIdUsuario(), userId, rol);
        return PagoResponse.desde(pago);
    }

    /**
     * Procesa el pago de un pedido con una pasarela simulada.
     * El token del usuario se reenvía a ms_pedido, así solo se puede pagar un pedido propio.
     */
    public PagoResponse procesarPago(PagoRequest req, Long userId, String authorization) {
        PedidoRemoto pedido = obtenerPedido(req.idPedido(), authorization);

        if (!userId.equals(pedido.idUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El pedido no pertenece al usuario");
        }
        if (!ESTADOS_PAGABLES.contains(pedido.estado())
                || repository.existsByIdPedidoAndEstado(req.idPedido(), EstadoPago.APROBADO)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El pedido no está pendiente de pago (estado: " + pedido.estado() + ")");
        }

        Pago pago = new Pago();
        pago.setIdPedido(req.idPedido());
        pago.setIdUsuario(userId);
        pago.setMonto(pedido.montoTotal());
        pago.setMetodo(req.metodo());
        pago.setEstado(Boolean.TRUE.equals(req.simularRechazo()) ? EstadoPago.RECHAZADO : EstadoPago.APROBADO);

        return PagoResponse.desde(repository.save(pago));
    }

    public void eliminar(Long id) {
        repository.delete(buscarPago(id));
    }

    private Pago buscarPago(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));
    }

    private PedidoRemoto obtenerPedido(Long idPedido, String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorization);
        try {
            PedidoRemoto pedido = restTemplate.exchange(pedidoUrl + "/api/pedidos/" + idPedido, HttpMethod.GET,
                    new HttpEntity<>(headers), PedidoRemoto.class).getBody();
            if (pedido == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ms-pedido devolvió una respuesta vacía");
            }
            return pedido;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pedido no existe: " + idPedido);
        } catch (HttpClientErrorException.Forbidden e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El pedido no pertenece al usuario");
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Servicio de pedidos no disponible");
        }
    }
}
