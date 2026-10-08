package cl.oursour.ms_recetas.controller;

import cl.oursour.ms_recetas.model.Ingrediente;
import cl.oursour.ms_recetas.service.IngredienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @GetMapping
    public ResponseEntity<List<Ingrediente>> listar() {
        return ResponseEntity.ok(ingredienteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingrediente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ingredienteService.buscarPorId(id));
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Ingrediente>> listarPorCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(ingredienteService.listarPorCategoria(categoria));
    }

    @PostMapping
    public ResponseEntity<Ingrediente> guardar(@RequestBody Ingrediente ingrediente) {
        Ingrediente nuevo = ingredienteService.guardar(ingrediente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingrediente> actualizar(@PathVariable Long id,
                                                  @RequestBody Ingrediente ingrediente) {
        return ResponseEntity.ok(ingredienteService.actualizar(id, ingrediente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ingredienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Lo usarán otros microservicios para comprobar que un ingrediente existe
    @GetMapping("/validar/{id}")
    public ResponseEntity<Boolean> validar(@PathVariable Long id) {
        return ResponseEntity.ok(ingredienteService.existe(id));
    }
}
