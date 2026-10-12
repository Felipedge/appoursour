package cl.oursour.ms_catalogo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.oursour.ms_catalogo.dto.ProductoResponse;
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

    

}
