package cl.oursour.ms_recetas.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
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

    


}
