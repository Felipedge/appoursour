package cl.oursour.ms_recetas.service;

import cl.oursour.ms_recetas.dto.IngredienteRequest;
import cl.oursour.ms_recetas.dto.IngredienteResponse;
import cl.oursour.ms_recetas.model.Ingrediente;
import cl.oursour.ms_recetas.repository.DetalleRecetaRepository;
import cl.oursour.ms_recetas.repository.IngredienteRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;

@Service
public class IngredienteService {

    private final IngredienteRepository repository;
    private final DetalleRecetaRepository detalleRepository;

    IngredienteService(IngredienteRepository repository, DetalleRecetaRepository detalleRepository) {
        this.repository = repository;
        this.detalleRepository = detalleRepository;
    }

    public List<IngredienteResponse> listarTodos() {
        return repository.findAll().stream().map(IngredienteResponse::desde).toList();
    }

    public IngredienteResponse buscarPorId(Long id) {
        return IngredienteResponse.desde(buscarIngrediente(id));
    }

    public List<IngredienteResponse> buscarPorCategoria(String categoria) {
        return repository.findByCategoria(categoria).stream().map(IngredienteResponse::desde).toList();
    }

    public IngredienteResponse guardar(IngredienteRequest req) {
        Ingrediente ingrediente = new Ingrediente();
        copiarDatos(req, ingrediente);
        return IngredienteResponse.desde(repository.save(ingrediente));
    }

    public IngredienteResponse actualizar(Long id, IngredienteRequest req) {
        Ingrediente ingrediente = buscarIngrediente(id);
        copiarDatos(req, ingrediente);
        return IngredienteResponse.desde(repository.save(ingrediente));
    }

    @Transactional
    public void eliminar(Long id) {
        Ingrediente ingrediente = buscarIngrediente(id);
        if (!detalleRepository.findByIdIngrediente(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: el ingrediente se usa en una o más recetas");
        }
        repository.delete(ingrediente);
    }

    public boolean validarIngrediente(Long id) {
        return repository.existsById(id);
    }

    // Usado por el motor de recomendación para traer los nombres de una sola vez
    public List<Ingrediente> buscarVarios(Collection<Long> ids) {
        return repository.findAllById(ids);
    }

    private Ingrediente buscarIngrediente(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingrediente no encontrado"));
    }

    private void copiarDatos(IngredienteRequest req, Ingrediente ingrediente) {
        ingrediente.setNombre(req.nombre());
        ingrediente.setCategoria(req.categoria());
        ingrediente.setIdProducto(req.idProducto());
    }
}
