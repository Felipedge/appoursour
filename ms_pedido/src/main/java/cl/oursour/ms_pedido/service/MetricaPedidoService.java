package cl.oursour.ms_pedido.service;

import cl.oursour.ms_pedido.dto.ConteoId;
import cl.oursour.ms_pedido.dto.VentasResponse;
import cl.oursour.ms_pedido.model.EstadoPedido;
import cl.oursour.ms_pedido.repository.PedidoProductoRepository;
import cl.oursour.ms_pedido.repository.PedidoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


@Service
public class MetricaPedidoService {

    
    private static final Set<EstadoPedido> PAGADOS = Set.of(EstadoPedido.SOLICITADO, EstadoPedido.EN_PREPARACION);

    private final PedidoRepository pedidoRepository;
    private final PedidoProductoRepository pedidoProductoRepository;

    public MetricaPedidoService(PedidoRepository pedidoRepository,
                                PedidoProductoRepository pedidoProductoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoProductoRepository = pedidoProductoRepository;
    }

    @Transactional(readOnly = true)
    public VentasResponse ventas() {
        BigDecimal total = pedidoRepository.sumarMontoTotal(PAGADOS);
        return new VentasResponse(pedidoRepository.countByEstadoIn(PAGADOS), total != null ? total : BigDecimal.ZERO);
    }

    @Transactional(readOnly = true)
    public List<ConteoId> productosMasVendidos(int limite) {
        int tope = Math.clamp(limite, 1, 100);
        return pedidoProductoRepository.productosMasVendidos(PAGADOS, PageRequest.of(0, tope));
    }
}
