package cl.oursour.ms_recetas.service;

import cl.oursour.ms_recetas.model.Ingrediente;
import cl.oursour.ms_recetas.repository.IngredienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class IngredienteService {

    private static final List<String> CATEGORIAS =
            List.of("DESTILADO", "LICOR", "FRUTA", "JUGO", "MIXER", "OUR_SOUR", "OTRO");

    private final IngredienteRepository ingredienteRepository;

    IngredienteService(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    public List<Ingrediente> listar() {
        return ingredienteRepository.findAll();
    }

    public List<Ingrediente> listarPorCategoria(String categoria) {
        String cat = normalizarCategoria(categoria);
        return ingredienteRepository.findByCategoria(cat);
    }

    public Ingrediente buscarPorId(Long id) {
        return ingredienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ingrediente no encontrado: " + id));
    }

    public Ingrediente guardar(Ingrediente ingrediente) {
        validarDatos(ingrediente);
        if (ingredienteRepository.existsByNombreIgnoreCase(ingrediente.getNombre().trim())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un ingrediente con ese nombre");
        }
        ingrediente.setIdIngrediente(null); // el id lo genera la base de datos
        ingrediente.setNombre(ingrediente.getNombre().trim());
        ingrediente.setCategoria(normalizarCategoria(ingrediente.getCategoria()));
        return ingredienteRepository.save(ingrediente);
    }

    public Ingrediente actualizar(Long id, Ingrediente datos) {
        Ingrediente existente = buscarPorId(id);
        validarDatos(datos);

        String nuevoNombre = datos.getNombre().trim();
        boolean cambioNombre = !existente.getNombre().equalsIgnoreCase(nuevoNombre);
        if (cambioNombre && ingredienteRepository.existsByNombreIgnoreCase(nuevoNombre)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe un ingrediente con ese nombre");
        }

        existente.setNombre(nuevoNombre);
        existente.setCategoria(normalizarCategoria(datos.getCategoria()));
        existente.setIdProducto(datos.getIdProducto());
        return ingredienteRepository.save(existente);
    }

    public void eliminar(Long id) {
        Ingrediente existente = buscarPorId(id);
        ingredienteRepository.delete(existente);
    }

    public boolean existe(Long id) {
        return ingredienteRepository.existsById(id);
    }

    // ---------- métodos privados de apoyo ----------

    private void validarDatos(Ingrediente ingrediente) {
        if (ingrediente.getNombre() == null || ingrediente.getNombre().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El nombre del ingrediente es obligatorio");
        }
        normalizarCategoria(ingrediente.getCategoria());
    }

    private String normalizarCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La categoría es obligatoria");
        }
        String cat = categoria.trim().toUpperCase();
        if (!CATEGORIAS.contains(cat)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Categoría inválida. Usa: " + CATEGORIAS);
        }
        return cat;
    }
}

