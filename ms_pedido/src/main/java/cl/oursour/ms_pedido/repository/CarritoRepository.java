package cl.oursour.ms_pedido.repository;

import cl.oursour.ms_pedido.model.Carrito;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Trae los productos junto al carrito: con open-in-view=false la lista lazy no se podría
// cargar al armar el JSON
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    @Override
    @EntityGraph(attributePaths = "productos")
    List<Carrito> findAll();

    @Override
    @EntityGraph(attributePaths = "productos")
    Optional<Carrito> findById(Long id);
}
