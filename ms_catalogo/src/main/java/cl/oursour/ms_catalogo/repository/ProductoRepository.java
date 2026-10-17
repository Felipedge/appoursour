package cl.oursour.ms_catalogo.repository;

import cl.oursour.ms_catalogo.model.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrueOrderByNombreComercialAsc();

    Optional<Producto> findByIdProductoAndActivoTrue(Long idProducto);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.idProducto IN :ids")
    List<Producto> findParaActualizar(Collection<Long> ids);
}
