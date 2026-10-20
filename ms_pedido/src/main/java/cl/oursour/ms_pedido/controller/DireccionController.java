package cl.oursour.ms_pedido.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.oursour.ms_pedido.dto.DireccionRequest;
import cl.oursour.ms_pedido.dto.DireccionResponse;
import cl.oursour.ms_pedido.security.Autorizacion;
import cl.oursour.ms_pedido.service.DireccionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {
    private final DireccionService service;

    DireccionController(DireccionService service) {
        this.service = service;
    }

    @GetMapping
    public List<DireccionResponse> listar(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                          @RequestHeader(Autorizacion.HEADER_ROL) String rol) {
        return service.listar(userId, rol);
    }

    @GetMapping("/{id}")
    public DireccionResponse buscarPorId(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                         @RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                         @PathVariable Long id) {
        return service.buscarPorId(id, userId, rol);
    }

    @PostMapping
    public ResponseEntity<DireccionResponse> guardar(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                                     @Valid @RequestBody DireccionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(userId, req));
    }

    @PutMapping("/{id}")
    public DireccionResponse actualizar(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                        @RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                        @PathVariable Long id,
                                        @Valid @RequestBody DireccionRequest req) {
        return service.actualizar(id, req, userId, rol);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                         @RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                         @PathVariable Long id) {
        service.eliminar(id, userId, rol);
        return ResponseEntity.noContent().build();
    }

}
