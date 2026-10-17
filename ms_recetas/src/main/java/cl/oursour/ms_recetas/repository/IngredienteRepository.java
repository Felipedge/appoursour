package cl.oursour.ms_recetas.repository;

import cl.oursour.ms_recetas.model.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    List<Ingrediente> findByCategoria(String categoria);
} 
