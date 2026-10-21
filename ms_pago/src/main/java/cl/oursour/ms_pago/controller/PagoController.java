package cl.oursour.ms_pago.controller;

import cl.oursour.ms_pago.dto.PagoRequest;
import cl.oursour.ms_pago.dto.PagoResponse;
import cl.oursour.ms_pago.security.Autorizacion;
import cl.oursour.ms_pago.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService service;

    PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PagoResponse> listarPagos(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return service.listarPagos(rol, authorization);
    }

    @GetMapping("/{id}")
    public PagoResponse buscarPorId(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                    @PathVariable Long id) {
        return service.buscarPorId(id, rol, authorization);
    }

    @PostMapping
    public ResponseEntity<PagoResponse> procesarPago(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                                     @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                     @Valid @RequestBody PagoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.procesarPago(req, userId, authorization));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                         @PathVariable Long id) {
        Autorizacion.exigirAdmin(rol);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
