package cl.oursour.ms_recetas.service;

import cl.oursour.ms_recetas.dto.IngredienteResponse;
import cl.oursour.ms_recetas.dto.RecomendacionResponse;
import cl.oursour.ms_recetas.model.DetalleReceta;
import cl.oursour.ms_recetas.model.Ingrediente;
import cl.oursour.ms_recetas.model.Receta;
import cl.oursour.ms_recetas.repository.RecetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {

    @Mock
    private RecetaRepository repository;

    @Mock
    private IngredienteService ingredienteService;

    private RecetaService service;

    @BeforeEach
    void setUp() {
        service = new RecetaService(repository, ingredienteService);
        when(ingredienteService.buscarVarios(any())).thenReturn(List.of(
                ingrediente(1L, "Pisco"), ingrediente(2L, "Limón"), ingrediente(3L, "Clara de huevo"),
                ingrediente(4L, "Ron"), ingrediente(5L, "Menta")));
    }

    @Test
    void ordenaPorPorcentajeDeCoincidenciaYDescartaLasSinCoincidencia() {
        Receta pisco = receta(1L, "Pisco Sour", 1L, 2L, 3L);
        Receta mojito = receta(2L, "Mojito", 4L, 2L, 5L);
        Receta piscola = receta(3L, "Piscola", 1L);
        Receta sinNada = receta(4L, "Daiquiri", 4L);
        when(repository.findAll()).thenReturn(List.of(pisco, mojito, piscola, sinNada));

        List<RecomendacionResponse> resultado = service.recomendar(List.of(1L, 2L));

        assertThat(resultado).extracting(RecomendacionResponse::titulo)
                .containsExactly("Piscola", "Pisco Sour", "Mojito");
        assertThat(resultado).extracting(RecomendacionResponse::porcentajeCoincidencia)
                .containsExactly(100.0, 66.7, 33.3);

        RecomendacionResponse piscoSour = resultado.get(1);
        assertThat(piscoSour.ingredientesQueTienes()).extracting(IngredienteResponse::nombre)
                .containsExactly("Pisco", "Limón");
        assertThat(piscoSour.ingredientesFaltantes()).extracting(IngredienteResponse::nombre)
                .containsExactly("Clara de huevo");
    }

    @Test
    void ingredienteRepetidoEnLaRecetaCuentaUnaSolaVez() {
        when(repository.findAll()).thenReturn(List.of(receta(1L, "Doble pisco", 1L, 1L, 2L)));

        List<RecomendacionResponse> resultado = service.recomendar(List.of(1L));

        assertThat(resultado).singleElement()
                .extracting(RecomendacionResponse::porcentajeCoincidencia).isEqualTo(50.0);
    }

    private static Receta receta(Long id, String titulo, Long... ingredientes) {
        Receta r = new Receta();
        r.setIdReceta(id);
        r.setTitulo(titulo);
        r.setTipo("SOUR");
        for (Long idIngrediente : ingredientes) {
            DetalleReceta d = new DetalleReceta();
            d.setIdIngrediente(idIngrediente);
            d.setDosificacion("1 oz");
            r.addDetalle(d);
        }
        return r;
    }

    private static Ingrediente ingrediente(Long id, String nombre) {
        Ingrediente i = new Ingrediente();
        i.setIdIngrediente(id);
        i.setNombre(nombre);
        return i;
    }
}
