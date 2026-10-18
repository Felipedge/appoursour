package cl.oursour.ms_pedido.controller;

import cl.oursour.ms_pedido.dto.EstadoRequest;
import cl.oursour.ms_pedido.dto.PedidoRequest;
import cl.oursour.ms_pedido.dto.PedidoResponse;
import cl.oursour.ms_pedido.security.Autorizacion;
import cl.oursour.ms_pedido.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PedidoResponse> listar(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                       @RequestHeader(Autorizacion.HEADER_ROL) String rol) {
        return service.listar(userId, rol);
    }

    // También lo usa ms_pago (reenviando el token del usuario) para validar el pedido y su monto
    @GetMapping("/{id}")
    public PedidoResponse obtener(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                  @RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                  @PathVariable Long id) {
        return service.obtener(id, userId, rol);
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                                @Valid @RequestBody PedidoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearPedido(userId, req));
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse actualizarEstado(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                           @PathVariable Long id,
                                           @Valid @RequestBody EstadoRequest req) {
        Autorizacion.exigirAdmin(rol);
        return service.actualizarEstado(id, req.estado());
    }

}
