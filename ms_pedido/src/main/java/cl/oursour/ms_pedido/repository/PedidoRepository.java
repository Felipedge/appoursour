package cl.oursour.ms_pedido.repository;

import cl.oursour.ms_pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
