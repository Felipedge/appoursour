package cl.oursour.ms_pedido.controller;

import cl.oursour.ms_pedido.dto.CantidadRequest;
import cl.oursour.ms_pedido.dto.CarritoResponse;
import cl.oursour.ms_pedido.dto.ProductoCarritoRequest;
import cl.oursour.ms_pedido.security.Autorizacion;
import cl.oursour.ms_pedido.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @GetMapping
    public CarritoResponse obtener(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId) {
        return service.obtener(userId);
    }

    @PostMapping("/productos")
    public CarritoResponse agregarProducto(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                           @Valid @RequestBody ProductoCarritoRequest req) {
        return service.agregarProducto(userId, req);
    }

    @PatchMapping("/productos/{idProducto}")
    public CarritoResponse cambiarCantidad(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                           @PathVariable Long idProducto,
                                           @Valid @RequestBody CantidadRequest req) {
        return service.cambiarCantidad(userId, idProducto, req.cantidad());
    }

    @DeleteMapping("/productos/{idProducto}")
    public CarritoResponse eliminarProducto(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId,
                                            @PathVariable Long idProducto) {
        return service.eliminarProducto(userId, idProducto);
    }

    @DeleteMapping
    public CarritoResponse vaciar(@RequestHeader(Autorizacion.HEADER_USUARIO) Long userId) {
        return service.vaciar(userId);
    }
}
