package cl.oursour.ms_pedido.repository;

import cl.oursour.ms_pedido.model.CarritoProducto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoProductoRepository extends JpaRepository<CarritoProducto, Long> {
}
