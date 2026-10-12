package cl.oursour.ms_recetas.service;

import cl.oursour.ms_recetas.model.DetalleReceta;
import cl.oursour.ms_recetas.model.Receta;
import cl.oursour.ms_recetas.repository.RecetaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecetaService {

    private final RecetaRepository repository;
    private final IngredienteService ingredienteService;

    RecetaService(RecetaRepository repository, IngredienteService ingredienteService) {
        this.repository = repository;
        this.ingredienteService = ingredienteService;
    }

    public List<Receta> listarTodas() {
        return repository.findAll();
    }

    public Optional<Receta> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Receta guardar(Receta receta) {

        List<DetalleReceta> detalles = receta.getDetalles();
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La receta debe contener al menos un ingrediente");
        }

        List<DetalleReceta> incoming = new ArrayList<>(detalles);
        receta.getDetalles().clear();
        incoming.forEach(receta::addDetalle);

        for (DetalleReceta d : receta.getDetalles()) {
            validarDetalle(d);
        }

        return repository.save(receta);
    }

    @Transactional
    public Receta agregarDetalle(Long id, DetalleReceta d) {
        Receta receta = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));

        validarDetalle(d);

        receta.addDetalle(d);
        return repository.save(receta);
    }

    public Receta actualizar(Long id, Receta recetaActualizada) {

        Receta receta = repository.findById(id).orElse(null);

        if (receta != null) {

            receta.setTitulo(recetaActualizada.getTitulo());
            receta.setTipo(recetaActualizada.getTipo());
            receta.setPasosPreparacion(recetaActualizada.getPasosPreparacion());
            receta.setIdProducto(recetaActualizada.getIdProducto());

            return repository.save(receta);
        }

        return null;
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
    }

    private void validarDetalle(DetalleReceta d) {
        if (d.getIdIngrediente() == null || d.getIdIngrediente() <= 0) {
            throw new IllegalArgumentException("idIngrediente es obligatorio en cada detalle");
        }
        if (!ingredienteService.validarIngrediente(d.getIdIngrediente())) {
            throw new IllegalArgumentException("Ingrediente no existe: " + d.getIdIngrediente());
        }
        if (d.getCantidad() <= 0) {
            throw new IllegalArgumentException("cantidad debe ser > 0");
        }
    }
}
