package cl.oursour.ms_recetas.repository;

import cl.oursour.ms_recetas.model.Receta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Las consultas que devuelven recetas traen también los detalles: con open-in-view=false
// la sesión ya está cerrada cuando se arma el JSON y la lista lazy no se podría cargar.
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    @Override
    @EntityGraph(attributePaths = "detalles")
    List<Receta> findAll();

    @Override
    @EntityGraph(attributePaths = "detalles")
    Optional<Receta> findById(Long id);

    @EntityGraph(attributePaths = "detalles")
    List<Receta> findByTipo(String tipo);
}
