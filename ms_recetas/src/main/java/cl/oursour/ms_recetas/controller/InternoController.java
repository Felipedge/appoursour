package cl.oursour.ms_recetas.controller;

import cl.oursour.ms_recetas.dto.IngredienteResponse;
import cl.oursour.ms_recetas.dto.RecetaResumen;
import cl.oursour.ms_recetas.service.IngredienteService;
import cl.oursour.ms_recetas.service.RecetaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Solo para microservicios (ms_metricas): SecurityConfig exige la clave interna en /internal/**
@RestController
@RequestMapping("/internal")
public class InternoController {

    private final IngredienteService ingredienteService;
    private final RecetaService recetaService;

    public InternoController(IngredienteService ingredienteService, RecetaService recetaService) {
        this.ingredienteService = ingredienteService;
        this.recetaService = recetaService;
    }

    @GetMapping("/ingredientes")
    public List<IngredienteResponse> ingredientes(@RequestParam List<Long> ids) {
        return ingredienteService.buscarVariosResponse(ids);
    }

    @GetMapping("/recetas")
    public List<RecetaResumen> recetas(@RequestParam List<Long> ids) {
        return recetaService.buscarVarias(ids);
    }
}
