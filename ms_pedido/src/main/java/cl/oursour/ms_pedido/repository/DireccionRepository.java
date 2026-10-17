package cl.oursour.ms_pedido.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.oursour.ms_pedido.model.DireccionDespacho;


public interface DireccionRepository extends JpaRepository<DireccionDespacho, Long> {

}