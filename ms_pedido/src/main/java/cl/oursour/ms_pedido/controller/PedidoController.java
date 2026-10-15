package cl.oursour.ms_pedido.controller;

import cl.oursour.ms_pedido.model.Pedido;
import cl.oursour.ms_pedido.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pedido> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Optional<Pedido> obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

     @PostMapping
    public ResponseEntity<?> crear(@RequestBody Pedido pedido) {
        try {
            return ResponseEntity.ok(service.crearPedido(pedido));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody String nuevoEstado) {
        try {
            return ResponseEntity.ok(service.actualizarEstado(id, nuevoEstado));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @GetMapping("/validar/{id}")
    public boolean validarPedido(@PathVariable Long id) {
        return service.validarPedido(id);
    }

}
