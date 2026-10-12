package cl.oursour.ms_catalogo.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import cl.oursour.ms_catalogo.model.Producto;
import jakarta.persistence.LockModeType;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByEstadoTrueOrderByNombreAsc();

    Optional<Producto> findByIdProductoAndEstadoTrue(Long idProducto);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.idProducto IN :ids")
    List<Producto> findParaActualizar(Collection<Long> ids);
}
