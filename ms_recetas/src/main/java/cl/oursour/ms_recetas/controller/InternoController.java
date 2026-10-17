package cl.oursour.ms_recetas.controller;

import cl.oursour.ms_recetas.dto.IngredienteResumen;
import cl.oursour.ms_recetas.dto.RecetaResumen;
import cl.oursour.ms_recetas.service.IngredienteService;
import cl.oursour.ms_recetas.service.RecetaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints que consumen otros microservicios (ms_metricas), no la app
@RestController
@RequestMapping("/internal")
public class InternoController {

    private final IngredienteService ingredienteService;
    private final RecetaService recetaService;

    InternoController(IngredienteService ingredienteService, RecetaService recetaService) {
        this.ingredienteService = ingredienteService;
        this.recetaService = recetaService;
    }

    @GetMapping("/ingredientes")
    public List<IngredienteResumen> ingredientesPorIds(@RequestParam List<Long> ids) {
        return ingredienteService.buscarPorIds(ids);
    }

    @GetMapping("/recetas")
    public List<RecetaResumen> recetasPorIds(@RequestParam List<Long> ids) {
        return recetaService.buscarPorIds(ids);
    }
}
