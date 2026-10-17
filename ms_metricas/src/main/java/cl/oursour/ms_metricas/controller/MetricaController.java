package cl.oursour.ms_metricas.controller;

import cl.oursour.ms_metricas.dto.CombinacionResponse;
import cl.oursour.ms_metricas.dto.ConteoResponse;
import cl.oursour.ms_metricas.dto.ResumenMetricasResponse;
import cl.oursour.ms_metricas.dto.TendenciaResponse;
import cl.oursour.ms_metricas.security.Autorizacion;
import cl.oursour.ms_metricas.service.MetricaService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/metricas")
public class MetricaController {

    private final MetricaService metricaService;

    public MetricaController(MetricaService metricaService) {
        this.metricaService = metricaService;
    }

    @GetMapping("/resumen")
    public ResumenMetricasResponse resumen(@RequestHeader(Autorizacion.HEADER_ROL) String rol) {
        Autorizacion.exigirAdmin(rol);
        return metricaService.resumen();
    }

    @GetMapping("/ingredientes-mas-buscados")
    public List<ConteoResponse> ingredientesMasBuscados(
            @RequestHeader(Autorizacion.HEADER_ROL) String rol,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limite) {
        Autorizacion.exigirAdmin(rol);
        return metricaService.ingredientesMasBuscados(limite);
    }

    @GetMapping("/recetas-mas-consultadas")
    public List<ConteoResponse> recetasMasConsultadas(
            @RequestHeader(Autorizacion.HEADER_ROL) String rol,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limite) {
        Autorizacion.exigirAdmin(rol);
        return metricaService.recetasMasConsultadas(limite);
    }

    @GetMapping("/combinaciones-frecuentes")
    public List<CombinacionResponse> combinacionesFrecuentes(
            @RequestHeader(Autorizacion.HEADER_ROL) String rol,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limite) {
        Autorizacion.exigirAdmin(rol);
        return metricaService.combinacionesFrecuentes(limite);
    }

    @GetMapping("/tendencias")
    public TendenciaResponse tendencias(
            @RequestHeader(Autorizacion.HEADER_ROL) String rol,
            @RequestParam(defaultValue = "30") @Min(1) @Max(365) int dias) {
        Autorizacion.exigirAdmin(rol);
        return metricaService.tendencias(dias);
    }

    @GetMapping("/productos-mas-vendidos")
    public List<ConteoResponse> productosMasVendidos(
            @RequestHeader(Autorizacion.HEADER_ROL) String rol,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int limite) {
        Autorizacion.exigirAdmin(rol);
        return metricaService.productosMasVendidos(limite);
    }
}
