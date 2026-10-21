package cl.oursour.ms_recetas.repository;

import cl.oursour.ms_recetas.dto.ConteoIngrediente;
import cl.oursour.ms_recetas.model.DetalleReceta;
import cl.oursour.ms_recetas.model.DetalleRecetaId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DetalleRecetaRepository extends JpaRepository<DetalleReceta, DetalleRecetaId> {

    boolean existsByIdIdIngrediente(Long idIngrediente);

   
    @Query("""
            select new cl.oursour.ms_recetas.dto.ConteoIngrediente(d.id.idIngrediente, count(d))
            from DetalleReceta d
            group by d.id.idIngrediente
            order by count(d) desc
            """)
    List<ConteoIngrediente> ingredientesFrecuentes(Pageable pageable);
}
