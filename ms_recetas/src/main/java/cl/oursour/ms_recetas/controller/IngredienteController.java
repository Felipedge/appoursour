package cl.oursour.ms_recetas.controller;

import cl.oursour.ms_recetas.model.Ingrediente;
import cl.oursour.ms_recetas.service.IngredienteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingredientes")
public class IngredienteController {

    private final IngredienteService service;

    IngredienteController(IngredienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Ingrediente> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<Ingrediente> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/categoria/{categoria}")
    public List<Ingrediente> buscarPorCategoria(@PathVariable String categoria) {
        return service.buscarPorCategoria(categoria);
    }

    @PostMapping
    public Ingrediente guardar(@RequestBody Ingrediente ingrediente) {
        return service.guardar(ingrediente);
    }

    @PutMapping("/{id}")
    public Ingrediente actualizar(@PathVariable Long id, @RequestBody Ingrediente ingrediente) {
        return service.actualizar(id, ingrediente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @GetMapping("/validar/{id}")
    public boolean validarIngrediente(@PathVariable Long id) {
        return service.validarIngrediente(id);
    }
}