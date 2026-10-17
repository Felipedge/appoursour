package cl.oursour.ms_recetas.service;

import cl.oursour.ms_recetas.model.Ingrediente;
import cl.oursour.ms_recetas.repository.IngredienteRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IngredienteService {

    private final IngredienteRepository repository;

    IngredienteService(IngredienteRepository repository) {
        this.repository = repository;
    }

    public List<Ingrediente> listarTodos() {
        return repository.findAll();
    }

    public Optional<Ingrediente> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public List<Ingrediente> buscarPorCategoria(String categoria) {
        return repository.findByCategoria(categoria);
    }

    public Ingrediente guardar(Ingrediente ingrediente) {
        return repository.save(ingrediente);
    }

    public Ingrediente actualizar(Long id, Ingrediente ingredienteActualizado) {

        Ingrediente ingrediente = repository.findById(id).orElse(null);

        if (ingrediente != null) {

            ingrediente.setNombre(ingredienteActualizado.getNombre());
            ingrediente.setCategoria(ingredienteActualizado.getCategoria());
            ingrediente.setIdProducto(ingredienteActualizado.getIdProducto());

            return repository.save(ingrediente);
        }

        return null;
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    public boolean validarIngrediente(Long id) {
        return repository.findById(id).isPresent();
    }
}

