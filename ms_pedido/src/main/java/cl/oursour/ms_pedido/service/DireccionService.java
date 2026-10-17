package cl.oursour.ms_pedido.service;

import cl.oursour.ms_pedido.model.DireccionDespacho;
import cl.oursour.ms_pedido.repository.DireccionRepository;
import cl.oursour.ms_pedido.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionService {

    private final DireccionRepository repository;

    private final PedidoRepository pedidoRepository;

    DireccionService(DireccionRepository repository, PedidoRepository pedidoRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<DireccionDespacho> listarTodas() {
        return repository.findAll();
    }

    public Optional<DireccionDespacho> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public DireccionDespacho guardar(DireccionDespacho direccion) {
        return repository.save(direccion);
    }

    public DireccionDespacho actualizar(Long id, DireccionDespacho direccionActualizada) {

        DireccionDespacho direccion = repository.findById(id).orElse(null);

        if (direccion != null) {

            direccion.setCalle(direccionActualizada.getCalle());
            direccion.setNumero(direccionActualizada.getNumero());
            direccion.setComuna(direccionActualizada.getComuna());
            direccion.setCiudad(direccionActualizada.getCiudad());
            direccion.setReferencia(direccionActualizada.getReferencia());

            return repository.save(direccion);
        }

        return null;
    }

    public void eliminar(Long id) {
        if (pedidoRepository.existsByIdDireccion(id)) {
            throw new IllegalStateException("No se puede eliminar la dirección: tiene pedidos asociados");
        }
        repository.deleteById(id);
    }
}
