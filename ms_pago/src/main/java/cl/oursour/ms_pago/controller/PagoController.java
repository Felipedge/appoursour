package cl.oursour.ms_pago.controller;

import cl.oursour.ms_pago.model.Pago;
import cl.oursour.ms_pago.service.PagoService;
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


    @PostMapping
    public ResponseEntity<?> procesarPago(@RequestBody Pago pago) {
        try {
            return ResponseEntity.ok(service.procesarPago(pago));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
