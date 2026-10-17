package cl.oursour.ms_metricas.repository;

import cl.oursour.ms_metricas.dto.ConteoId;
import cl.oursour.ms_metricas.dto.ParConteo;
import cl.oursour.ms_metricas.model.Busqueda;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BusquedaRepository extends JpaRepository<Busqueda, Long> {

    @Query("""
            SELECT new cl.oursour.ms_metricas.dto.ConteoId(bi.id.idIngrediente, COUNT(bi))
            FROM BusquedaIngrediente bi
            GROUP BY bi.id.idIngrediente
            ORDER BY COUNT(bi) DESC
            """)
    List<ConteoId> ingredientesMasBuscados(Pageable pageable);

    @Query("""
            SELECT new cl.oursour.ms_metricas.dto.ConteoId(bi.id.idIngrediente, COUNT(bi))
            FROM BusquedaIngrediente bi
            JOIN bi.busqueda b
            WHERE b.fechaBusqueda >= :desde
            GROUP BY bi.id.idIngrediente
            ORDER BY COUNT(bi) DESC
            """)
    List<ConteoId> ingredientesMasBuscadosDesde(LocalDateTime desde, Pageable pageable);

    @Query("""
            SELECT new cl.oursour.ms_metricas.dto.ParConteo(a.id.idIngrediente, b.id.idIngrediente, COUNT(a))
            FROM BusquedaIngrediente a, BusquedaIngrediente b
            WHERE a.busqueda = b.busqueda
              AND a.id.idIngrediente < b.id.idIngrediente
            GROUP BY a.id.idIngrediente, b.id.idIngrediente
            ORDER BY COUNT(a) DESC
            """)
    List<ParConteo> combinacionesFrecuentes(Pageable pageable);

    @Query("SELECT b.fechaBusqueda FROM Busqueda b WHERE b.fechaBusqueda >= :desde")
    List<LocalDateTime> fechasDesde(LocalDateTime desde);
}
