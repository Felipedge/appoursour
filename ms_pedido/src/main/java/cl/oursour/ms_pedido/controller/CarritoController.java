package cl.oursour.ms_pedido.controller;

import cl.oursour.ms_pedido.dto.CantidadRequest;
import cl.oursour.ms_pedido.dto.CarritoRequest;
import cl.oursour.ms_pedido.dto.CarritoResponse;
import cl.oursour.ms_pedido.dto.ProductoCarritoRequest;
import cl.oursour.ms_pedido.security.Autorizacion;
import cl.oursour.ms_pedido.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @GetMapping
    public List<CarritoResponse> listar(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                        @RequestHeader(Autorizacion.HEADER_ROL) String rol) {
        return service.listar(userId, rol);
    }

    @GetMapping("/{id}")
    public CarritoResponse obtener(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                   @RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                   @PathVariable Long id) {
        return service.obtener(id, userId, rol);
    }

    @PostMapping
    public ResponseEntity<CarritoResponse> crear(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                                 @Valid @RequestBody CarritoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearCarrito(userId, req));
    }

    @PostMapping("/{id}/productos")
    public CarritoResponse agregarProducto(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                           @RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                           @PathVariable Long id,
                                           @Valid @RequestBody ProductoCarritoRequest req) {
        return service.agregarProducto(id, req, userId, rol);
    }

    @PatchMapping("/{id}/productos/{idDetalle}")
    public CarritoResponse cambiarCantidad(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                           @RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                           @PathVariable Long id, @PathVariable Long idDetalle,
                                           @Valid @RequestBody CantidadRequest req) {
        return service.cambiarCantidad(id, idDetalle, req.cantidad(), userId, rol);
    }

    @DeleteMapping("/{id}/productos/{idDetalle}")
    public CarritoResponse eliminarProducto(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                            @RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                            @PathVariable Long id, @PathVariable Long idDetalle) {
        return service.eliminarProducto(id, idDetalle, userId, rol);
    }

    @DeleteMapping("/{id}/productos")
    public CarritoResponse vaciar(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                  @RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                  @PathVariable Long id) {
        return service.vaciar(id, userId, rol);
    }
}
