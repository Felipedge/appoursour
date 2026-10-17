package cl.oursour.ms_recetas.repository;

import cl.oursour.ms_recetas.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecetaRepository extends JpaRepository<Receta, Long> {

    List<Receta> findByTipo(String tipo);
}
