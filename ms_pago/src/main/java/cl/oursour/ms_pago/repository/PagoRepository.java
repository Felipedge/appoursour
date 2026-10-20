package cl.oursour.ms_pago.repository;

import cl.oursour.ms_pago.model.Pago;
import cl.oursour.ms_pago.model.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByIdPedidoIn(Collection<Long> idsPedido);

    boolean existsByIdPedidoAndEstado(Long idPedido, EstadoPago estado);
}
