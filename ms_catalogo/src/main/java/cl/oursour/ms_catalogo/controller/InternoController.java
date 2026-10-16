package cl.oursour.ms_catalogo.controller;

import cl.oursour.ms_catalogo.dto.DescuentoStockRequest;
import cl.oursour.ms_catalogo.dto.ProductoResponse;
import cl.oursour.ms_catalogo.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/internal/productos")
public class InternoController {

    private final ProductoService productoService;

    public InternoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoResponse> buscarPorIds(@RequestParam List<Long> ids) {
        return productoService.buscarPorIds(ids);
    }

    @PostMapping("/descontar-stock")
    public ResponseEntity<Void> descontarStock(@Valid @RequestBody DescuentoStockRequest req) {
        productoService.descontarStock(req);
        return ResponseEntity.noContent().build();
    }
}
