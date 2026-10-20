package cl.oursour.ms_pedido.repository;

import cl.oursour.ms_pedido.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    
    Optional<Carrito> findFirstByIdUsuarioOrderByIdCarritoDesc(Long idUsuario);
}
