package cl.oursour.ms_pedido.repository;

import cl.oursour.ms_pedido.dto.ConteoId;
import cl.oursour.ms_pedido.model.EstadoPedido;
import cl.oursour.ms_pedido.model.PedidoProducto;
import cl.oursour.ms_pedido.model.PedidoProductoId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;


public interface PedidoProductoRepository extends JpaRepository<PedidoProducto, PedidoProductoId> {

        @Query("""
            select new cl.oursour.ms_pedido.dto.ConteoId(d.id.idProducto, sum(d.cantidad))
            from PedidoProducto d
            where d.pedido.estado in :estados
            group by d.id.idProducto
            order by sum(d.cantidad) desc
            """)
    List<ConteoId> productosMasVendidos(@Param("estados") Collection<EstadoPedido> estados, Pageable pageable);
}
