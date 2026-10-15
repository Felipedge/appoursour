package cl.oursour.ms_metricas.service;

import cl.oursour.ms_metricas.client.RemotoClient;
import cl.oursour.ms_metricas.dto.CombinacionResponse;
import cl.oursour.ms_metricas.dto.ConteoId;
import cl.oursour.ms_metricas.dto.ConteoResponse;
import cl.oursour.ms_metricas.dto.ParConteo;
import cl.oursour.ms_metricas.dto.TendenciaResponse;
import cl.oursour.ms_metricas.repository.BusquedaRepository;
import cl.oursour.ms_metricas.repository.ConsultaRecetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetricaServiceTest {

    @Mock
    private BusquedaRepository busquedaRepository;
    @Mock
    private ConsultaRecetaRepository consultaRecetaRepository;
    @Mock
    private RemotoClient remotoClient;

    private MetricaService service;

    @BeforeEach
    void setUp() {
        service = new MetricaService(busquedaRepository, consultaRecetaRepository, remotoClient);
    }

    @Test
    void combinacionesFrecuentesAgreganNombresDeIngredientes() {
        when(busquedaRepository.combinacionesFrecuentes(any()))
                .thenReturn(List.of(new ParConteo(4L, 7L, 12L), new ParConteo(1L, 7L, 5L)));
        when(remotoClient.nombresIngredientes(anyCollection()))
                .thenReturn(Map.of(1L, "Pisco", 4L, "Gin", 7L, "Limón"));

        List<CombinacionResponse> combinaciones = service.combinacionesFrecuentes(10);

        assertThat(combinaciones).containsExactly(
                new CombinacionResponse(4L, "Gin", 7L, "Limón", 12L),
                new CombinacionResponse(1L, "Pisco", 7L, "Limón", 5L));
    }

    @Test
    void tendenciasIncluyenLosDiasSinBusquedasConCero() {
        LocalDate hoy = LocalDate.of(2026, 10, 10);
        when(busquedaRepository.fechasDesde(any())).thenReturn(List.of(
                hoy.atTime(10, 0), hoy.atTime(18, 30),          // 2 hoy
                hoy.minusDays(2).atTime(12, 0)));               // 1 hace dos días
        when(busquedaRepository.ingredientesMasBuscadosDesde(any(), any()))
                .thenReturn(List.of(new ConteoId(4L, 3L)));
        when(remotoClient.nombresIngredientes(anyCollection())).thenReturn(Map.of(4L, "Gin"));

        TendenciaResponse tendencia = service.tendencias(3, hoy);

        assertThat(tendencia.desde()).isEqualTo(LocalDate.of(2026, 10, 8));
        assertThat(tendencia.hasta()).isEqualTo(hoy);
        assertThat(tendencia.totalBusquedas()).isEqualTo(3);
        assertThat(tendencia.busquedasPorDia()).containsExactly(
                new TendenciaResponse.Dia(LocalDate.of(2026, 10, 8), 1),
                new TendenciaResponse.Dia(LocalDate.of(2026, 10, 9), 0),
                new TendenciaResponse.Dia(hoy, 2));
        assertThat(tendencia.ingredientesDelPeriodo()).containsExactly(new ConteoResponse(4L, "Gin", 3L));
    }

    @Test
    void siMsRecetasNoRespondeElRankingSeEntregaSinNombres() {
        when(busquedaRepository.ingredientesMasBuscados(any())).thenReturn(List.of(new ConteoId(4L, 9L)));
        when(remotoClient.nombresIngredientes(anyCollection()))
                .thenThrow(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "ms-recetas no está disponible"));

        List<ConteoResponse> ranking = service.ingredientesMasBuscados(10);

        assertThat(ranking).containsExactly(new ConteoResponse(4L, null, 9L));
    }
}
