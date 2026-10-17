package cl.oursour.ms_pago.repository;

import cl.oursour.ms_pago.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
