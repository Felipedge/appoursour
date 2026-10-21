package cl.oursour.ms_recetas.controller;

import cl.oursour.ms_recetas.dto.IngredienteFrecuenteResponse;
import cl.oursour.ms_recetas.dto.IngredienteRequest;
import cl.oursour.ms_recetas.dto.IngredienteResponse;
import cl.oursour.ms_recetas.security.Autorizacion;
import cl.oursour.ms_recetas.service.IngredienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredientes")
public class IngredienteController {

    private final IngredienteService service;

    IngredienteController(IngredienteService service) {
        this.service = service;
    }

    
    @GetMapping
    public List<IngredienteResponse> listar(@RequestParam(required = false) String categoria) {
        return categoria == null || categoria.isBlank()
                ? service.listarTodos()
                : service.buscarPorCategoria(categoria);
    }

    @GetMapping("/frecuentes")
    public List<IngredienteFrecuenteResponse> frecuentes(@RequestParam(defaultValue = "10") int limite) {
        return service.frecuentes(limite);
    }

    @GetMapping("/{id}")
    public IngredienteResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<IngredienteResponse> guardar(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                                       @Valid @RequestBody IngredienteRequest req) {
        Autorizacion.exigirAdmin(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(req));
    }

    @PutMapping("/{id}")
    public IngredienteResponse actualizar(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                          @PathVariable Long id,
                                          @Valid @RequestBody IngredienteRequest req) {
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

    @GetMapping("/validar/{id}")
    public boolean validarIngrediente(@PathVariable Long id) {
        return service.validarIngrediente(id);
    }
}
