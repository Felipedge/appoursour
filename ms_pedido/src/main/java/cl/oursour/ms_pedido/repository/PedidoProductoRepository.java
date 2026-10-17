package cl.oursour.ms_pedido.repository;

import cl.oursour.ms_pedido.model.PedidoProducto;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PedidoProductoRepository extends JpaRepository<PedidoProducto, Long> {
}
