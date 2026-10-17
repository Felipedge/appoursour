package cl.oursour.ms_pedido.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import cl.oursour.ms_pedido.dto.ConteoId;
import cl.oursour.ms_pedido.dto.VentasResponse;
import cl.oursour.ms_pedido.repository.PedidoProductoRepository;
import cl.oursour.ms_pedido.repository.PedidoRepository;

// Datos de ventas que pide ms_metricas por /internal/metricas
@Service
public class MetricaPedidoService {

    // Un pedido cuenta como venta cuando su pago fue aprobado
    private static final List<String> ESTADOS_PAGADOS = List.of("SOLICITADO", "EN_PREPARACION");
    private static final int LIMITE_MAXIMO = 100;

    private final PedidoRepository pedidoRepository;
    private final PedidoProductoRepository pedidoProductoRepository;

    public MetricaPedidoService(PedidoRepository pedidoRepository, PedidoProductoRepository pedidoProductoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoProductoRepository = pedidoProductoRepository;
    }

    public VentasResponse ventas() {
        long pedidosPagados = pedidoRepository.countByEstadoIn(ESTADOS_PAGADOS);
        long total = pedidoRepository.sumarMontoTotalPorEstados(ESTADOS_PAGADOS);
        return new VentasResponse(pedidosPagados, BigDecimal.valueOf(total));
    }

    public List<ConteoId> productosMasVendidos(int limite) {
        int tamanio = Math.max(1, Math.min(limite, LIMITE_MAXIMO));
        return pedidoProductoRepository.productosMasVendidos(ESTADOS_PAGADOS, PageRequest.of(0, tamanio));
    }
}
