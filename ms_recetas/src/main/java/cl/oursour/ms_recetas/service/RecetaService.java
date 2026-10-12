package cl.oursour.ms_recetas.service;


import java.util.List;

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

}
