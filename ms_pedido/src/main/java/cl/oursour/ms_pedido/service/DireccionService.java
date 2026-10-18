package cl.oursour.ms_pedido.service;

import cl.oursour.ms_pedido.dto.DireccionRequest;
import cl.oursour.ms_pedido.dto.DireccionResponse;
import cl.oursour.ms_pedido.model.DireccionDespacho;
import cl.oursour.ms_pedido.repository.DireccionRepository;
import cl.oursour.ms_pedido.repository.PedidoRepository;
import cl.oursour.ms_pedido.security.Autorizacion;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DireccionService {

    private final DireccionRepository repository;
    private final PedidoRepository pedidoRepository;

    DireccionService(DireccionRepository repository, PedidoRepository pedidoRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
    }

    // El ADMIN ve todas las direcciones; un consumidor solo las suyas
    public List<DireccionResponse> listar(Long userId, String rol) {
        List<DireccionDespacho> direcciones = Autorizacion.esAdmin(rol)
                ? repository.findAll()
                : repository.findByIdUsuario(userId);
        return direcciones.stream().map(DireccionResponse::desde).toList();
    }

    public DireccionResponse buscarPorId(Long id, Long userId, String rol) {
        return DireccionResponse.desde(buscarDireccion(id, userId, rol));
    }

    // El dueño de la dirección es siempre el usuario del token
    public DireccionResponse guardar(Long userId, DireccionRequest req) {
        DireccionDespacho direccion = new DireccionDespacho();
        direccion.setIdUsuario(userId);
        copiarDatos(req, direccion);
        return DireccionResponse.desde(repository.save(direccion));
    }

    public DireccionResponse actualizar(Long id, DireccionRequest req, Long userId, String rol) {
        DireccionDespacho direccion = buscarDireccion(id, userId, rol);
        copiarDatos(req, direccion);
        return DireccionResponse.desde(repository.save(direccion));
    }

    @Transactional
    public void eliminar(Long id, Long userId, String rol) {
        DireccionDespacho direccion = buscarDireccion(id, userId, rol);
        if (pedidoRepository.existsByIdDireccion(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar: la dirección está asociada a uno o más pedidos");
        }
        repository.delete(direccion);
    }

    // Usado al crear un pedido: la dirección debe existir y ser del comprador
    DireccionDespacho buscarDelUsuario(Long id, Long userId) {
        DireccionDespacho direccion = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dirección no existe"));
        if (!userId.equals(direccion.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La dirección no pertenece al usuario");
        }
        return direccion;
    }

    private DireccionDespacho buscarDireccion(Long id, Long userId, String rol) {
        DireccionDespacho direccion = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dirección no encontrada"));
        Autorizacion.exigirDuenoOAdmin(direccion.getIdUsuario(), userId, rol);
        return direccion;
    }

    private void copiarDatos(DireccionRequest req, DireccionDespacho direccion) {
        direccion.setCalle(req.calle());
        direccion.setNumero(req.numero());
        direccion.setComuna(req.comuna());
        direccion.setCiudad(req.ciudad());
        direccion.setReferencia(req.referencia());
    }
}
