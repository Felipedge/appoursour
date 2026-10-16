package cl.oursour.ms_pago.service;

import cl.oursour.ms_pago.model.Pago;
import cl.oursour.ms_pago.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
