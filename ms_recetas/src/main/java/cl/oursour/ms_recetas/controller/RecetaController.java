package cl.oursour.ms_recetas.controller;

import cl.oursour.ms_recetas.model.DetalleReceta;
import cl.oursour.ms_recetas.model.Receta;
import cl.oursour.ms_recetas.service.RecetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recetas")
public class RecetaController {

    private final RecetaService service;

    RecetaController(RecetaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Receta> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public Optional<Receta> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Receta receta) {
        try {
            return ResponseEntity.ok(service.guardar(receta));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/detalles")
    public ResponseEntity<?> agregarDetalle(@PathVariable Long id, @RequestBody DetalleReceta detalle) {
        try {
            return ResponseEntity.ok(service.agregarDetalle(id, detalle));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Receta actualizar(@PathVariable Long id, @RequestBody Receta receta) {
        return service.actualizar(id, receta);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}

