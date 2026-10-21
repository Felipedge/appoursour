package cl.oursour.ms_recetas.service;

import cl.oursour.ms_recetas.client.CatalogoClient;
import cl.oursour.ms_recetas.client.MetricasClient;
import cl.oursour.ms_recetas.dto.DetalleRequest;
import cl.oursour.ms_recetas.dto.IngredienteResponse;
import cl.oursour.ms_recetas.dto.RecetaDetalleResponse;
import cl.oursour.ms_recetas.dto.RecetaRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecetaServiceTest {

    @Mock
    private RecetaRepository repository;

    @Mock
    private IngredienteService ingredienteService;

    @Mock
    private MetricasClient metricasClient;

    @Mock
    private CatalogoClient catalogoClient;

    private RecetaService service;

    @BeforeEach
    void setUp() {
        service = new RecetaService(repository, ingredienteService, metricasClient, catalogoClient);
        lenient().when(ingredienteService.buscarVarios(any())).thenReturn(List.of(
                ingrediente(1L, "Pisco", null), ingrediente(2L, "Limón", null), ingrediente(3L, "Clara de huevo", null),
                ingrediente(4L, "Ron", null), ingrediente(5L, "Menta", null), ingrediente(6L, "Our Sour Clásico", 1L)));
    }

    @Test
    void recomendarOrdenaPorCoincidenciaYDescartaLasSinCoincidencia() {
        when(repository.findAll()).thenReturn(List.of(
                receta(1L, "Pisco Sour", 1L, 2L, 3L), receta(2L, "Mojito", 4L, 2L, 5L),
                receta(3L, "Piscola", 1L), receta(4L, "Daiquiri", 4L)));

        List<RecomendacionResponse> resultado = service.recomendar(List.of(1L, 2L), 7L);

        assertThat(resultado).extracting(RecomendacionResponse::titulo)
                .containsExactly("Piscola", "Pisco Sour", "Mojito");
        assertThat(resultado).extracting(RecomendacionResponse::porcentajeCoincidencia)
                .containsExactly(100.0, 66.7, 33.3);
        assertThat(resultado.get(1).ingredientesFaltantes()).extracting(IngredienteResponse::nombre)
                .containsExactly("Clara de huevo");
    }

    @Test
    void recomendarRegistraLaBusquedaEnMetricas() {
        when(repository.findAll()).thenReturn(List.of());

        service.recomendar(List.of(1L, 2L, 2L), 7L);

        verify(metricasClient).registrarBusqueda(7L, Set.of(1L, 2L));
    }

    @Test
    void detalleIncluyeNombresProductosSugeridosYRegistraLaConsulta() {
        Receta r = receta(1L, "Pisco Sour", 1L, 6L);
        r.setIdProducto(2L);
        when(repository.findById(1L)).thenReturn(Optional.of(r));
        when(catalogoClient.productos(any())).thenReturn(List.of(
                new CatalogoClient.Producto(2L, "Our Sour Maracuyá", new BigDecimal("7000"), true),
                new CatalogoClient.Producto(1L, "Our Sour Clásico", new BigDecimal("7990"), false)));

        RecetaDetalleResponse detalle = service.buscarPorId(1L, 7L);

        assertThat(detalle.ingredientes()).extracting(RecetaDetalleResponse.Ingrediente::nombre)
                .containsExactly("Pisco", "Our Sour Clásico");
        // El producto inactivo no se sugiere
        assertThat(detalle.productosSugeridos()).extracting(RecetaDetalleResponse.ProductoSugerido::nombreComercial)
                .containsExactly("Our Sour Maracuyá");
        verify(catalogoClient).productos(Set.of(2L, 1L));
        verify(metricasClient).registrarConsulta(7L, 1L);
    }

    @Test
    void recetaInexistenteDa404() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L, 7L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(metricasClient, never()).registrarConsulta(anyLong(), anyLong());
    }

    @Test
    void ingredienteRepetidoEnLaRecetaDa400() {
        RecetaRequest req = new RecetaRequest("Doble pisco", "SOUR", "Batir", null,
                List.of(new DetalleRequest(1L, "1 oz"), new DetalleRequest(1L, "2 oz")));

        assertThatThrownBy(() -> service.guardar(req))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void actualizarConservaLosDetallesExistentesYCambiaLaDosificacion() {
        Receta r = receta(1L, "Pisco Sour", 1L, 2L);
        DetalleReceta pisco = r.getDetalles().get(0);
        when(repository.findById(1L)).thenReturn(Optional.of(r));
        when(ingredienteService.validarIngrediente(3L)).thenReturn(true);
        when(repository.save(any(Receta.class))).thenAnswer(inv -> inv.getArgument(0));

        service.actualizar(1L, new RecetaRequest("Pisco Sour", "SOUR", "Batir", null,
                List.of(new DetalleRequest(1L, "3 oz"), new DetalleRequest(3L, "1 clara"))));

        assertThat(r.getDetalles()).extracting(DetalleReceta::getIdIngrediente).containsExactly(1L, 3L);
        // Misma instancia: no se borra y se vuelve a insertar la misma clave
        assertThat(r.getDetalles().get(0)).isSameAs(pisco);
        assertThat(pisco.getDosificacion()).isEqualTo("3 oz");
    }

    @Test
    void agregarIngredienteQueYaEstaDa409() {
        when(repository.findById(1L)).thenReturn(Optional.of(receta(1L, "Pisco Sour", 1L)));

        assertThatThrownBy(() -> service.agregarDetalle(1L, new DetalleRequest(1L, "1 oz")))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
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

    private static Ingrediente ingrediente(Long id, String nombre, Long idProducto) {
        Ingrediente i = new Ingrediente();
        i.setIdIngrediente(id);
        i.setNombre(nombre);
        i.setIdProducto(idProducto);
        return i;
    }
}
