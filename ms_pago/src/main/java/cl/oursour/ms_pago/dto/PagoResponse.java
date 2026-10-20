package cl.oursour.ms_pago.dto;

import cl.oursour.ms_pago.model.EstadoPago;
import cl.oursour.ms_pago.model.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long idPago,
        Long idPedido,
        BigDecimal monto,
        String metodo,
        EstadoPago estado,
        LocalDateTime fechaPago
) {
    public static PagoResponse desde(Pago p) {
        return new PagoResponse(p.getIdPago(), p.getIdPedido(), p.getMonto(),
                p.getMetodo(), p.getEstado(), p.getFechaPago());
    }
}
