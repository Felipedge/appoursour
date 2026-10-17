package cl.oursour.ms_catalogo.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import cl.oursour.ms_catalogo.dto.DescuentoStockRequest;
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
    @Transactional
    public ProductoResponse actualizar(Long idProducto, ProductoRequest req) {
        Producto producto = buscarActivo(idProducto);
        producto.setNombreComercial(req.nombreComercial().trim());
        producto.setDescripcion(req.descripcion());
        producto.setPrecioUnitario(req.precioUnitario());
        producto.setStockSimulado(req.stockSimulado());
        return ProductoResponse.desde(producto);
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
    @Transactional
    public void descontarStock(DescuentoStockRequest req) {
        Map<Long, Integer> cantidades = req.items().stream()
                .collect(Collectors.toMap(i -> i.idProducto(), i -> i.cantidad(), Integer::sum));

        Map<Long, Producto> productos = productoRepository.findParaActualizar(cantidades.keySet()).stream()
                .collect(Collectors.toMap(p -> p.getIdProducto(), Function.identity()));

        cantidades.forEach((id, cantidad) -> {
            Producto p = productos.get(id);
            if (p == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto " + id + " no encontrado");
            }
            if (cantidad > p.getStockSimulado()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock insuficiente para "
                        + p.getNombreComercial() + " (disponible: " + p.getStockSimulado() + ")");
            }
        });
        cantidades.forEach((id, cantidad) -> productos.get(id).descontarStock(cantidad));
    }

    @Transactional
    public void eliminar(Long idProducto) {
        buscarActivo(idProducto).setActivo(false);
    }
                
    @Transactional(readOnly = true)
    public List <ProductoResponse> buscarPorIds (List<Long> ids) {
        return productoRepository.findAllById(ids).stream().map(ProductoResponse::desde)
            .toList();
    }
    private Producto buscarActivo (Long idProducto) {
        return productoRepository.findByIdProductoAndActivoTrue(idProducto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

}
