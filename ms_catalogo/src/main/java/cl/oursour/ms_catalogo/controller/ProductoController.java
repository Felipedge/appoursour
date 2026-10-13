package cl.oursour.ms_catalogo.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.ms_catalogo.dto.ProductoRequest;
import cl.oursour.ms_catalogo.dto.ProductoResponse;
import cl.oursour.ms_catalogo.security.Autorizacion;
import cl.oursour.ms_catalogo.service.ProductoService;
import jakarta.validation.Valid;
@RestController 
@RequestMapping ("/api/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }
    @GetMapping ("/{idProducto}")
    public ProductoResponse obtener (@PathVariable Long idProducto) {
        return productoService.obtener(idProducto);
    }
        
    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.listar();
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@RequestHeader(Autorizacion.HEADER_ROL) String rol, @Valid @RequestBody ProductoRequest req) {
        Autorizacion.exigirAdmin(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(req));
    }

    @PutMapping("/{idProducto}")
    public ProductoResponse actualizar(@RequestHeader(Autorizacion.HEADER_ROL) String rol, @PathVariable Long idProducto, @Valid @RequestBody ProductoRequest req) {
        Autorizacion.exigirAdmin(rol);
        return productoService.actualizar(idProducto, req);
    }


    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Void> eliminar(@RequestHeader(Autorizacion.HEADER_ROL) String rol,
                                         @PathVariable Long idProducto) {
        Autorizacion.exigirAdmin(rol);
        productoService.eliminar(idProducto);
        return ResponseEntity.noContent().build();
    }


}
