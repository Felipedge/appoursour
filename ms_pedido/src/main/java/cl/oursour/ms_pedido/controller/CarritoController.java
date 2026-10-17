package cl.oursour.ms_pedido.controller;

import cl.oursour.ms_pedido.model.Carrito;
import cl.oursour.ms_pedido.model.CarritoProducto;
import cl.oursour.ms_pedido.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Carrito> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Optional<Carrito> obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Carrito carrito) {
        try {
            return ResponseEntity.ok(service.crearCarrito(carrito));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<?> agregarProducto(@PathVariable Long id, @RequestBody CarritoProducto producto) {
        try {
            return ResponseEntity.ok(service.agregarProducto(id, producto));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PatchMapping("/{id}/productos/{idDetalle}")
    public ResponseEntity<?> cambiarCantidad(@PathVariable Long id, @PathVariable Long idDetalle,
                                             @RequestBody CarritoProducto producto) {
        try {
            return ResponseEntity.ok(service.cambiarCantidad(id, idDetalle, producto.getCantidad()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}/productos/{idDetalle}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id, @PathVariable Long idDetalle) {
        try {
            return ResponseEntity.ok(service.eliminarProducto(id, idDetalle));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}/productos")
    public ResponseEntity<?> vaciar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.vaciar(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}