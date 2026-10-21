package cl.oursour.ms_recetas.service;

import cl.oursour.ms_recetas.client.CatalogoClient;
import cl.oursour.ms_recetas.client.MetricasClient;
import cl.oursour.ms_recetas.dto.DetalleRequest;
import cl.oursour.ms_recetas.dto.IngredienteResponse;
import cl.oursour.ms_recetas.dto.RecetaDetalleResponse;
import cl.oursour.ms_recetas.dto.RecetaRequest;
import cl.oursour.ms_recetas.dto.RecetaResponse;
import cl.oursour.ms_recetas.dto.RecetaResumen;
import cl.oursour.ms_recetas.dto.RecomendacionResponse;
import cl.oursour.ms_recetas.model.DetalleReceta;
import cl.oursour.ms_recetas.model.Ingrediente;
import cl.oursour.ms_recetas.model.Receta;
import cl.oursour.ms_recetas.repository.RecetaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecetaService {

    private final RecetaRepository repository;
    private final IngredienteService ingredienteService;
    private final MetricasClient metricasClient;
    private final CatalogoClient catalogoClient;

    RecetaService(RecetaRepository repository, IngredienteService ingredienteService,
                  MetricasClient metricasClient, CatalogoClient catalogoClient) {
        this.repository = repository;
        this.ingredienteService = ingredienteService;
        this.metricasClient = metricasClient;
        this.catalogoClient = catalogoClient;
    }

    @Transactional(readOnly = true)
    public List<RecetaResponse> listarTodas() {
        return repository.findAll().stream().map(RecetaResponse::desde).toList();
    }

    
    @Transactional(readOnly = true)
    public RecetaDetalleResponse buscarPorId(Long id, Long userId) {
        Receta receta = buscarReceta(id);

        List<Long> idsIngredientes = receta.getDetalles().stream().map(DetalleReceta::getIdIngrediente).toList();
        Map<Long, Ingrediente> ingredientes = mapaIngredientes(idsIngredientes);

        List<RecetaDetalleResponse.Ingrediente> lista = receta.getDetalles().stream()
                .map(d -> {
                    Ingrediente i = ingredientes.get(d.getIdIngrediente());
                    return new RecetaDetalleResponse.Ingrediente(d.getIdIngrediente(),
                            i != null ? i.getNombre() : null, i != null ? i.getCategoria() : null,
                            d.getDosificacion());
                })
                .toList();

       
        Set<Long> idsProductos = new LinkedHashSet<>();
        if (receta.getIdProducto() != null) {
            idsProductos.add(receta.getIdProducto());
        }
        ingredientes.values().stream().map(Ingrediente::getIdProducto).filter(Objects::nonNull)
                .forEach(idsProductos::add);

        List<RecetaDetalleResponse.ProductoSugerido> sugeridos = catalogoClient.productos(idsProductos).stream()
                .filter(p -> !Boolean.FALSE.equals(p.activo()))
                .map(p -> new RecetaDetalleResponse.ProductoSugerido(p.idProducto(), p.nombreComercial(),
                        p.precioUnitario()))
                .toList();

        metricasClient.registrarConsulta(userId, id);

        return new RecetaDetalleResponse(receta.getIdReceta(), receta.getTitulo(), receta.getTipo(),
                receta.getPasosPreparacion(), receta.getIdProducto(), lista, sugeridos);
    }

    @Transactional(readOnly = true)
    public List<RecetaResumen> buscarVarias(Collection<Long> ids) {
        return repository.findAllById(ids).stream().map(RecetaResumen::desde).toList();
    }

    @Transactional
    public RecetaResponse guardar(RecetaRequest req) {
        Receta receta = new Receta();
        copiarDatos(req, receta);
        return RecetaResponse.desde(repository.save(receta));
    }

    @Transactional
    public RecetaResponse agregarDetalle(Long id, DetalleRequest req) {
        Receta receta = buscarReceta(id);
        boolean yaExiste = receta.getDetalles().stream()
                .anyMatch(d -> d.getIdIngrediente().equals(req.idIngrediente()));
        if (yaExiste) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El ingrediente " + req.idIngrediente() + " ya está en la receta");
        }
        receta.addDetalle(crearDetalle(req));
        return RecetaResponse.desde(repository.save(receta));
    }

    @Transactional
    public RecetaResponse actualizar(Long id, RecetaRequest req) {
        Receta receta = buscarReceta(id);
        copiarDatos(req, receta);
        return RecetaResponse.desde(repository.save(receta));
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(buscarReceta(id));
    }

    
    @Transactional(readOnly = true)
    public List<RecomendacionResponse> recomendar(List<Long> ingredientesUsuario, Long userId) {
        Set<Long> disponibles = new HashSet<>(ingredientesUsuario);
        List<Receta> recetas = repository.findAll();

        Set<Long> idsUsados = recetas.stream()
                .flatMap(r -> r.getDetalles().stream())
                .map(DetalleReceta::getIdIngrediente)
                .collect(Collectors.toSet());
        Map<Long, Ingrediente> ingredientes = mapaIngredientes(idsUsados);

        List<RecomendacionResponse> resultado = new ArrayList<>();
        for (Receta receta : recetas) {
            Set<Long> requeridos = receta.getDetalles().stream()
                    .map(DetalleReceta::getIdIngrediente)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (requeridos.isEmpty()) {
                continue;
            }

            List<IngredienteResponse> tiene = new ArrayList<>();
            List<IngredienteResponse> falta = new ArrayList<>();
            for (Long idIngrediente : requeridos) {
                IngredienteResponse ing = aRespuesta(idIngrediente, ingredientes);
                (disponibles.contains(idIngrediente) ? tiene : falta).add(ing);
            }
            if (tiene.isEmpty()) {
                continue;
            }

            double porcentaje = Math.round(tiene.size() * 1000.0 / requeridos.size()) / 10.0;
            resultado.add(new RecomendacionResponse(receta.getIdReceta(), receta.getTitulo(), receta.getTipo(),
                    receta.getIdProducto(), porcentaje, tiene, falta));
        }

        resultado.sort(Comparator.comparingDouble(RecomendacionResponse::porcentajeCoincidencia).reversed()
                .thenComparingInt(r -> r.ingredientesFaltantes().size())
                .thenComparing(RecomendacionResponse::titulo, Comparator.nullsLast(String::compareToIgnoreCase)));

        metricasClient.registrarBusqueda(userId, disponibles);
        return resultado;
    }

    private Receta buscarReceta(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receta no encontrada"));
    }

    private Map<Long, Ingrediente> mapaIngredientes(Collection<Long> ids) {
        return ingredienteService.buscarVarios(ids).stream()
                .collect(Collectors.toMap(Ingrediente::getIdIngrediente, Function.identity()));
    }

    private void copiarDatos(RecetaRequest req, Receta receta) {
        receta.setTitulo(req.titulo());
        receta.setTipo(req.tipo());
        receta.setPasosPreparacion(req.pasosPreparacion());
        receta.setIdProducto(req.idProducto());
        sincronizarDetalles(receta, req.detalles());
    }

    private void sincronizarDetalles(Receta receta, List<DetalleRequest> nuevos) {
        Map<Long, DetalleRequest> porIngrediente = new LinkedHashMap<>();
        for (DetalleRequest d : nuevos) {
            if (porIngrediente.put(d.idIngrediente(), d) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El ingrediente " + d.idIngrediente() + " está repetido en la receta");
            }
        }

        new ArrayList<>(receta.getDetalles()).stream()
                .filter(d -> !porIngrediente.containsKey(d.getIdIngrediente()))
                .forEach(receta::removeDetalle);

        for (DetalleRequest req : porIngrediente.values()) {
            receta.getDetalles().stream()
                    .filter(d -> d.getIdIngrediente().equals(req.idIngrediente()))
                    .findFirst()
                    .ifPresentOrElse(d -> d.setDosificacion(req.dosificacion()),
                            () -> receta.addDetalle(crearDetalle(req)));
        }
    }

    private DetalleReceta crearDetalle(DetalleRequest req) {
        if (!ingredienteService.validarIngrediente(req.idIngrediente())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ingrediente no existe: " + req.idIngrediente());
        }
        DetalleReceta detalle = new DetalleReceta();
        detalle.setIdIngrediente(req.idIngrediente());
        detalle.setDosificacion(req.dosificacion());
        return detalle;
    }

    private static IngredienteResponse aRespuesta(Long id, Map<Long, Ingrediente> ingredientes) {
        Ingrediente i = ingredientes.get(id);
        return i != null ? IngredienteResponse.desde(i) : new IngredienteResponse(id, null, null, null);
    }
}
