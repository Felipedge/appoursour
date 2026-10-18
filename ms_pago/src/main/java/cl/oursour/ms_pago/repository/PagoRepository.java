package cl.oursour.ms_pago.repository;

import cl.oursour.ms_pago.model.Pago;
import cl.oursour.ms_pago.model.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByIdUsuario(Long idUsuario);

    boolean existsByIdPedidoAndEstado(Long idPedido, EstadoPago estado);
}
