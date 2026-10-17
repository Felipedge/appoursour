package cl.oursour.ms_metricas.controller;

import cl.oursour.ms_metricas.dto.BusquedaRequest;
import cl.oursour.ms_metricas.dto.ConsultaRequest;
import cl.oursour.ms_metricas.service.MetricaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternoController {

    private final MetricaService metricaService;

    public InternoController(MetricaService metricaService) {
        this.metricaService = metricaService;
    }

    @PostMapping("/busquedas")
    public ResponseEntity<Void> registrarBusqueda(@Valid @RequestBody BusquedaRequest req) {
        metricaService.registrarBusqueda(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/consultas")
    public ResponseEntity<Void> registrarConsulta(@Valid @RequestBody ConsultaRequest req) {
        metricaService.registrarConsulta(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
