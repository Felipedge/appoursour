package cl.oursour.ms_metricas.repository;

import cl.oursour.ms_metricas.dto.ConteoId;
import cl.oursour.ms_metricas.model.ConsultaReceta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConsultaRecetaRepository extends JpaRepository<ConsultaReceta, Long> {

    @Query("""
            SELECT new cl.oursour.ms_metricas.dto.ConteoId(c.idReceta, COUNT(c))
            FROM ConsultaReceta c
            GROUP BY c.idReceta
            ORDER BY COUNT(c) DESC
            """)
    List<ConteoId> recetasMasConsultadas(Pageable pageable);
}
