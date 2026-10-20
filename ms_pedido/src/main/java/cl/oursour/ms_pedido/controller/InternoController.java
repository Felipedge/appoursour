package cl.oursour.ms_pedido.controller;

import cl.oursour.ms_pedido.dto.ConteoId;
import cl.oursour.ms_pedido.dto.PedidoResponse;
import cl.oursour.ms_pedido.dto.ResultadoPagoRequest;
import cl.oursour.ms_pedido.dto.VentasResponse;
import cl.oursour.ms_pedido.service.MetricaPedidoService;
import cl.oursour.ms_pedido.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/internal")
public class InternoController {

    private final MetricaPedidoService service;
    private final PedidoService pedidoService;

    public InternoController(MetricaPedidoService service, PedidoService pedidoService) {
        this.service = service;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/metricas/ventas")
    public VentasResponse ventas() {
        return service.ventas();
    }

    @GetMapping("/metricas/productos-mas-vendidos")
    public List<ConteoId> productosMasVendidos(@RequestParam(defaultValue = "10") int limite) {
        return service.productosMasVendidos(limite);
    }

    
    @PostMapping("/pedidos/{id}/resultado-pago")
    public PedidoResponse resultadoPago(@PathVariable Long id, @Valid @RequestBody ResultadoPagoRequest req) {
        return pedidoService.registrarResultadoPago(id, req.aprobado());
    }
}
