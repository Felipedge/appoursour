package cl.oursour.ms_pedido.controller;

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

import cl.oursour.ms_pedido.model.DireccionDespacho;
import cl.oursour.ms_pedido.service.DireccionService;

@RestController 
@RequestMapping("/direccion")
public class DireccionController {
    private final DireccionService service;

    DireccionController(DireccionService service) {
        this.service = service;
    }

    @GetMapping
    public List<DireccionDespacho> listarTodas() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public Optional<DireccionDespacho> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public DireccionDespacho guardar(@RequestBody DireccionDespacho direccion) {
        return service.guardar(direccion);
    }

    @PutMapping("/{id}")
    public DireccionDespacho actualizar(@PathVariable Long id, @RequestBody DireccionDespacho direccion) {
        return service.actualizar(id, direccion);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

}
