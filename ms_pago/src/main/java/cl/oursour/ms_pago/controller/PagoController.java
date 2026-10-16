package cl.oursour.ms_pago.controller;

import cl.oursour.ms_pago.model.Pago;
import cl.oursour.ms_pago.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService service;

    PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pago> listarPagos() {
        return service.listarPagos();
    }

    @GetMapping("/{id}")
    public Optional<Pago> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    

}
