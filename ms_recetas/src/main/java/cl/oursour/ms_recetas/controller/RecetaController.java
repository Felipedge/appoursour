package cl.oursour.ms_recetas.controller;

import cl.oursour.ms_recetas.dto.DetalleRequest;
import cl.oursour.ms_recetas.dto.RecetaRequest;
import cl.oursour.ms_recetas.dto.RecetaResponse;
import cl.oursour.ms_recetas.dto.RecomendacionRequest;
import cl.oursour.ms_recetas.dto.RecomendacionResponse;
import cl.oursour.ms_recetas.security.Autorizacion;
import cl.oursour.ms_recetas.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private final RecetaService service;

    RecetaController(RecetaService service) {
        this.service = service;
    }

    @GetMapping
    public List<RecetaResponse> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public RecetaResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    
    @PostMapping("/recomendar")
    public List<RecomendacionResponse> recomendar(@Valid @RequestBody RecomendacionRequest req) {
        return service.recomendar(req.ingredientes());
    }

    @PostMapping
    public ResponseEntity<RecetaResponse> guardar(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                                  @Valid @RequestBody RecetaRequest req) {
        Autorizacion.exigirAdmin(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(req));
    }

    @PostMapping("/{id}/detalles")
    public RecetaResponse agregarDetalle(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                         @PathVariable Long id,
                                         @Valid @RequestBody DetalleRequest req) {
        Autorizacion.exigirAdmin(rol);
        return service.agregarDetalle(id, req);
    }

    @PutMapping("/{id}")
    public RecetaResponse actualizar(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                     @PathVariable Long id,
                                     @Valid @RequestBody RecetaRequest req) {
        Autorizacion.exigirAdmin(rol);
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                         @PathVariable Long id) {
        Autorizacion.exigirAdmin(rol);
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
