package cl.oursour.ms_recetas.service;

import cl.oursour.ms_recetas.dto.DetalleRequest;
import cl.oursour.ms_recetas.dto.IngredienteResponse;
import cl.oursour.ms_recetas.dto.RecetaRequest;
import cl.oursour.ms_recetas.dto.RecetaResponse;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecetaService {

    private final RecetaRepository repository;
    private final IngredienteService ingredienteService;

    RecetaService(RecetaRepository repository, IngredienteService ingredienteService) {
        this.repository = repository;
        this.ingredienteService = ingredienteService;
    }

    @Transactional(readOnly = true)
    public List<RecetaResponse> listarTodas() {
        return repository.findAll().stream().map(RecetaResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public RecetaResponse buscarPorId(Long id) {
        return RecetaResponse.desde(buscarReceta(id));
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
    public List<RecomendacionResponse> recomendar(List<Long> ingredientesUsuario) {
        Set<Long> disponibles = new HashSet<>(ingredientesUsuario);
        List<Receta> recetas = repository.findAll();

        
        Set<Long> idsUsados = recetas.stream()
                .flatMap(r -> r.getDetalles().stream())
                .map(DetalleReceta::getIdIngrediente)
                .collect(Collectors.toSet());
        Map<Long, Ingrediente> ingredientes = ingredienteService.buscarVarios(idsUsados).stream()
                .collect(Collectors.toMap(Ingrediente::getIdIngrediente, Function.identity()));

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
        return resultado;
    }

    private Receta buscarReceta(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receta no encontrada"));
    }

    private void copiarDatos(RecetaRequest req, Receta receta) {
        receta.setTitulo(req.titulo());
        receta.setTipo(req.tipo());
        receta.setPasosPreparacion(req.pasosPreparacion());
        receta.setIdProducto(req.idProducto());
        receta.setDetalles(req.detalles().stream().map(this::crearDetalle).toList());
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
