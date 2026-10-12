package cl.oursour.ms_catalogo.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.oursour.ms_catalogo.dto.ProductoRequest;
import cl.oursour.ms_catalogo.dto.ProductoResponse;
import cl.oursour.ms_catalogo.model.Producto;
import cl.oursour.ms_catalogo.repository.ProductoRepository;

@Service 
public class ProductoService {
    private final ProductoRepository productoRepository;
 
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findByActivoTrueOrderByNombreComercialAsc().stream()
                .map(ProductoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long idProducto) {
        return ProductoResponse.desde(buscarActivo(idProducto));
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest req) {
        Producto producto = new Producto(req.nombreComercial().trim(), req.descripcion(),
                req.precioUnitario(), req.stockSimulado());
        return ProductoResponse.desde(productoRepository.save(producto));
    }

    private Producto buscarActivo(Long idProducto) {
        return productoRepository.findByIdProductoAndActivoTrue(idProducto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

}
