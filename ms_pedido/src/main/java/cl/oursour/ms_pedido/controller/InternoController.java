package cl.oursour.ms_pedido.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.ms_pedido.dto.ConteoId;
import cl.oursour.ms_pedido.dto.VentasResponse;
import cl.oursour.ms_pedido.service.MetricaPedidoService;

// Endpoints que consumen otros microservicios (ms_metricas), no la app
@RestController
@RequestMapping("/internal/metricas")
public class InternoController {

    private final MetricaPedidoService service;

    InternoController(MetricaPedidoService service) {
        this.service = service;
    }

    @GetMapping("/ventas")
    public VentasResponse ventas() {
        return service.ventas();
    }

    @GetMapping("/productos-mas-vendidos")
    public List<ConteoId> productosMasVendidos(@RequestParam(defaultValue = "5") int limite) {
        return service.productosMasVendidos(limite);
    }
}
