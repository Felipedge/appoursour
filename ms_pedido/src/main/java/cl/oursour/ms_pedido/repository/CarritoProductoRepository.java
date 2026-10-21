package cl.oursour.ms_pedido.repository;

import cl.oursour.ms_pedido.model.CarritoProducto;
import cl.oursour.ms_pedido.model.CarritoProductoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoProductoRepository extends JpaRepository<CarritoProducto, CarritoProductoId> {
}
