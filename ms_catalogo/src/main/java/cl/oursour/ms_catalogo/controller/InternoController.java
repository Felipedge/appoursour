package cl.oursour.ms_catalogo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.ms_catalogo.dto.DescuentoStockRequest;
import cl.oursour.ms_catalogo.dto.ProductoResponse;
import cl.oursour.ms_catalogo.service.ProductoService;
import jakarta.validation.Valid;

@RestController 
@RequestMapping ("/interno/productos")
public class InternoController {
    private final ProductoService productoService;

    public InternoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping 
    public List <ProductoResponse> buscarPorIds (@RequestParam List <Long> ids) {
        return productoService.buscarPorIds(ids);
    }

    @PostMapping ("/descontar-stock")
    public ResponseEntity <Void> descontarStock (@Valid @RequestBody DescuentoStockRequest req) {
        productoService.descontarStock(req);
        return ResponseEntity.noContent().build();
    }
    


}
