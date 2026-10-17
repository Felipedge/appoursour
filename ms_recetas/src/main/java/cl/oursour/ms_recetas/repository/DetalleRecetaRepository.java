package cl.oursour.ms_recetas.repository;

import cl.oursour.ms_recetas.model.DetalleReceta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleRecetaRepository extends JpaRepository<DetalleReceta, Long> {

    List<DetalleReceta> findByIdIngrediente(Long idIngrediente);

    List<DetalleReceta> findByRecetaIdReceta(Long idReceta);
}