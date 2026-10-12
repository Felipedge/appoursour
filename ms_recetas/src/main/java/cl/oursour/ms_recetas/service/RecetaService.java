package cl.oursour.ms_recetas.service;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cl.oursour.ms_recetas.model.Receta;
import cl.oursour.ms_recetas.repository.RecetaRepository;


@Service 
public class RecetaService {

    private final RecetaRepository repository;

    RecetaService(RecetaRepository repository){
        this.repository = repository;

    }

    public List<Receta> listarTodas(){
        return  repository.findAll();
    }

     public Optional<Receta> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Receta guardar(Receta receta) {
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

}
