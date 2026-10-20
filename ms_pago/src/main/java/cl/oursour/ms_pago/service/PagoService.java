package cl.oursour.ms_pago.service;

import cl.oursour.ms_pago.client.PedidoClient;
import cl.oursour.ms_pago.dto.PagoRequest;
import cl.oursour.ms_pago.dto.PagoResponse;
import cl.oursour.ms_pago.dto.PedidoRemoto;
import cl.oursour.ms_pago.model.EstadoPago;
import cl.oursour.ms_pago.model.Pago;
import cl.oursour.ms_pago.repository.PagoRepository;
import cl.oursour.ms_pago.security.Autorizacion;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class PagoService {

    private static final Set<String> ESTADOS_PAGABLES = Set.of("PENDIENTE_PAGO", "PAGO_RECHAZADO");
    private static final String SUFIJO_RECHAZO = "0000";

    private final PagoRepository repository;
    private final PedidoClient pedidoClient;

    PagoService(PagoRepository repository, PedidoClient pedidoClient) {
        this.repository = repository;
        this.pedidoClient = pedidoClient;
    }

    
    public List<PagoResponse> listarPagos(String rol, String authorization) {
        List<Pago> pagos = Autorizacion.esAdmin(rol)
                ? repository.findAll()
                : repository.findByIdPedidoIn(pedidoClient.misPedidos(authorization).stream()
                        .map(PedidoRemoto::idPedido).toList());
        return pagos.stream().map(PagoResponse::desde).toList();
    }

    public PagoResponse buscarPorId(Long id, String rol, String authorization) {
        Pago pago = buscarPago(id);
        if (!Autorizacion.esAdmin(rol)) {
            pedidoClient.obtener(pago.getIdPedido(), authorization);
        }
        return PagoResponse.desde(pago);
    }

    public PagoResponse procesarPago(PagoRequest req, Long userId, String authorization) {
        PedidoRemoto pedido = pedidoClient.obtener(req.idPedido(), authorization);

        if (!userId.equals(pedido.idUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El pedido no pertenece al usuario");
        }
        if (!ESTADOS_PAGABLES.contains(pedido.estado())
                || repository.existsByIdPedidoAndEstado(req.idPedido(), EstadoPago.APROBADO)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El pedido no está pendiente de pago (estado: " + pedido.estado() + ")");
        }

        boolean aprobado = !req.numeroTarjeta().endsWith(SUFIJO_RECHAZO);

       
        pedidoClient.registrarResultado(req.idPedido(), aprobado);

        Pago pago = new Pago();
        pago.setIdPedido(req.idPedido());
        pago.setMonto(pedido.montoTotal());
        pago.setMetodo(req.metodo());
        pago.setEstado(aprobado ? EstadoPago.APROBADO : EstadoPago.RECHAZADO);
        return PagoResponse.desde(repository.save(pago));
    }

    public void eliminar(Long id) {
        repository.delete(buscarPago(id));
    }

    private Pago buscarPago(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pago no encontrado"));
    }
}
