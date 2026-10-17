package cl.oursour.ms_pedido.repository;

import cl.oursour.ms_pedido.dto.ConteoId;
import cl.oursour.ms_pedido.model.PedidoProducto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PedidoProductoRepository extends JpaRepository<PedidoProducto, Long> {

    // Unidades vendidas por producto, solo de pedidos en los estados indicados
    @Query("""
            SELECT new cl.oursour.ms_pedido.dto.ConteoId(d.idProducto, SUM(d.cantidad))
            FROM PedidoProducto d
            WHERE d.pedido.estado IN :estados
            GROUP BY d.idProducto
            ORDER BY SUM(d.cantidad) DESC
            """)
    List<ConteoId> productosMasVendidos(@Param("estados") List<String> estados, Pageable pageable);
}
