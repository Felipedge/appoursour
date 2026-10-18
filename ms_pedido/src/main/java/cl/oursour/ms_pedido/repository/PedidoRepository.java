package cl.oursour.ms_pedido.repository;

import cl.oursour.ms_pedido.model.EstadoPedido;
import cl.oursour.ms_pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByIdUsuario(Long idUsuario);

    boolean existsByIdDireccion(Long idDireccion);

    long countByEstadoIn(Collection<EstadoPedido> estados);

    // null si no hay pedidos en esos estados
    @Query("select sum(p.montoTotal) from Pedido p where p.estado in :estados")
    BigDecimal sumarMontoTotal(@Param("estados") Collection<EstadoPedido> estados);
}
