package cl.oursour.ms_pedido.repository;

import cl.oursour.ms_pedido.model.Pedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// Trae los productos junto al pedido: con open-in-view=false la lista lazy no se podría
// cargar al armar el JSON
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Override
    @EntityGraph(attributePaths = "productos")
    List<Pedido> findAll();

    @Override
    @EntityGraph(attributePaths = "productos")
    Optional<Pedido> findById(Long id);

    long countByEstadoIn(List<String> estados);

    boolean existsByIdDireccion(Long idDireccion);

    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Pedido p WHERE p.estado IN :estados")
    long sumarMontoTotalPorEstados(@Param("estados") List<String> estados);
}
