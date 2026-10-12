package cl.oursour.ms_recetas.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.ms_recetas.model.Receta;
import cl.oursour.ms_recetas.service.RecetaService;

@RestController 
@RequestMapping("receta")
public class RecetaController {

    private final RecetaService service;

    RecetaController(RecetaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Receta> listarTodas(){
        return service.listarTodas(); 
    }

    @GetMapping("/{id}")
    public Optional<Receta> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Receta guardar(@RequestBody Receta receta) {
        return service.guardar(receta);
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
