package cl.oursour.ms_pago.service;

import cl.oursour.ms_pago.model.Pago;
import cl.oursour.ms_pago.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    // Cambia el puerto por el que use tu ms_pedido
    private static final String PEDIDO_VALIDAR_URL = "http://localhost:8092/pedidos/validar/";

    private final PagoRepository repository;

    private final RestTemplate restTemplate;

    PagoService(PagoRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public List<Pago> listarPagos() {
        return repository.findAll();
    }

     public Optional<Pago> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Pago procesarPago(Pago pago) {
        if (pago.getIdPedido() == null || pago.getIdPedido() <= 0) {
            throw new IllegalArgumentException("idPedido es obligatorio");
        }
        if (pago.getMonto() <= 0) {
            throw new IllegalArgumentException("monto debe ser mayor que 0");
        }

        validarPedido(pago.getIdPedido());

        return repository.save(pago);
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private void validarPedido(Long idPedido) {
        try {
            Boolean existe = restTemplate.getForObject(PEDIDO_VALIDAR_URL + idPedido, Boolean.class);
            if (!Boolean.TRUE.equals(existe)) {
                throw new IllegalArgumentException("Pedido no existe: " + idPedido);
            }
        } catch (RestClientException e) {
            throw new IllegalArgumentException("Servicio de pedidos no disponible: " + e.getMessage());
        }
    }
}
