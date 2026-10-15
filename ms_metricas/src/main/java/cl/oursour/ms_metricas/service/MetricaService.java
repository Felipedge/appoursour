package cl.oursour.ms_metricas.service;

public class MetricaService {

}
package cl.oursour.ms_metricas.service;

import cl.oursour.ms_metricas.client.RemotoClient;
import cl.oursour.ms_metricas.dto.BusquedaRequest;
import cl.oursour.ms_metricas.dto.CombinacionResponse;
import cl.oursour.ms_metricas.dto.ConsultaRequest;
import cl.oursour.ms_metricas.dto.ConteoId;
import cl.oursour.ms_metricas.dto.ConteoResponse;
import cl.oursour.ms_metricas.dto.ParConteo;
import cl.oursour.ms_metricas.dto.ResumenMetricasResponse;
import cl.oursour.ms_metricas.dto.TendenciaResponse;
import cl.oursour.ms_metricas.model.Busqueda;
import cl.oursour.ms_metricas.model.ConsultaReceta;
import cl.oursour.ms_metricas.repository.BusquedaRepository;
import cl.oursour.ms_metricas.repository.ConsultaRecetaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class MetricaService {

    private static final Logger log = LoggerFactory.getLogger(MetricaService.class);

    private final BusquedaRepository busquedaRepository;
    private final ConsultaRecetaRepository consultaRecetaRepository;
    private final RemotoClient remotoClient;

    public MetricaService(BusquedaRepository busquedaRepository,
                          ConsultaRecetaRepository consultaRecetaRepository,
                          RemotoClient remotoClient) {
        this.busquedaRepository = busquedaRepository;
        this.consultaRecetaRepository = consultaRecetaRepository;
        this.remotoClient = remotoClient;
    }

    @Transactional
    public void registrarBusqueda(BusquedaRequest req) {
        Busqueda busqueda = new Busqueda(req.idUsuario());
        req.idsIngredientes().forEach(busqueda::agregarIngrediente);
        busquedaRepository.save(busqueda);
    }

    @Transactional
    public void registrarConsulta(ConsultaRequest req) {
        consultaRecetaRepository.save(new ConsultaReceta(req.idUsuario(), req.idReceta()));
    }

    @Transactional(readOnly = true)
    public List<ConteoResponse> ingredientesMasBuscados(int limite) {
        List<ConteoId> conteos = busquedaRepository.ingredientesMasBuscados(PageRequest.of(0, limite));
        return conNombres(conteos, ids -> remotoClient.nombresIngredientes(ids));
    }

    @Transactional(readOnly = true)
    public List<ConteoResponse> recetasMasConsultadas(int limite) {
        List<ConteoId> conteos = consultaRecetaRepository.recetasMasConsultadas(PageRequest.of(0, limite));
        return conNombres(conteos, ids -> remotoClient.titulosRecetas(ids));
    }

    public List<ConteoResponse> productosMasVendidos(int limite) {
        return conNombres(remotoClient.productosMasVendidos(limite), ids -> remotoClient.nombresProductos(ids));
    }

    @Transactional(readOnly = true)
    public List<CombinacionResponse> combinacionesFrecuentes(int limite) {
        List<ParConteo> pares = busquedaRepository.combinacionesFrecuentes(PageRequest.of(0, limite));
        Set<Long> ids = new HashSet<>();
        pares.forEach(p -> {
            ids.add(p.idIngrediente1());
            ids.add(p.idIngrediente2());
        });
        Map<Long, String> nombres = nombresSeguros(() -> remotoClient.nombresIngredientes(ids));
        return pares.stream()
                .map(p -> new CombinacionResponse(p.idIngrediente1(), nombres.get(p.idIngrediente1()),
                        p.idIngrediente2(), nombres.get(p.idIngrediente2()), p.cantidad()))
                .toList();
    }

    @Transactional(readOnly = true)
    public TendenciaResponse tendencias(int dias) {
        return tendencias(dias, LocalDate.now());
    }

    TendenciaResponse tendencias(int dias, LocalDate hoy) {
        LocalDate desde = hoy.minusDays(dias - 1L);
        LocalDateTime inicio = desde.atStartOfDay();

        Map<LocalDate, Long> porDia = busquedaRepository.fechasDesde(inicio).stream()
                .collect(Collectors.groupingBy(f -> f.toLocalDate(), Collectors.counting()));
        List<TendenciaResponse.Dia> serie = desde.datesUntil(hoy.plusDays(1))
                .map(d -> new TendenciaResponse.Dia(d, porDia.getOrDefault(d, 0L)))
                .toList();
        long total = serie.stream().mapToLong(d -> d.busquedas()).sum();

        List<ConteoId> top = busquedaRepository.ingredientesMasBuscadosDesde(inicio, PageRequest.of(0, 5));
        return new TendenciaResponse(desde, hoy, total, serie,
                conNombres(top, ids -> remotoClient.nombresIngredientes(ids)));
    }

    @Transactional(readOnly = true)
    public ResumenMetricasResponse resumen() {
        RemotoClient.Ventas ventas = remotoClient.ventas();
        return new ResumenMetricasResponse(busquedaRepository.count(), consultaRecetaRepository.count(),
                ventas.pedidosPagados(), ventas.ventasTotales());
    }

    private List<ConteoResponse> conNombres(List<ConteoId> conteos, Function<List<Long>, Map<Long, String>> buscarNombres) {
        List<Long> ids = conteos.stream().map(c -> c.id()).toList();
        Map<Long, String> nombres = nombresSeguros(() -> buscarNombres.apply(ids));
        return conteos.stream()
                .map(c -> new ConteoResponse(c.id(), nombres.get(c.id()), c.cantidad()))
                .toList();
    }

    private static Map<Long, String> nombresSeguros(Supplier<Map<Long, String>> buscarNombres) {
        try {
            return buscarNombres.get();
        } catch (ResponseStatusException ex) {
            log.warn("Métrica sin nombres: {}", ex.getReason());
            return Map.of();
        }
    }
}
