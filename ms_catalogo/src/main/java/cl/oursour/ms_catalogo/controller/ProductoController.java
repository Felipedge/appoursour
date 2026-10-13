package cl.oursour.ms_catalogo.controller;

import java.util.List;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.ms_catalogo.dto.ProductoResponse;
import cl.oursour.ms_catalogo.service.ProductoService;
@RestController 
@RequestMapping ("/api/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }
    @GetMapping ("/{idProducto}")
    public ProductoResponse obtener (Long idProducto) {
        return productoService.obtener(idProducto);
    }
        
    @GetMapping
    public List<ProductoResponse> listar() {
        return productoService.listar();
    }

    
}
