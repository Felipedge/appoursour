package cl.oursour.ms_pedido.dto;

import cl.oursour.ms_pedido.model.DireccionDespacho;

public record DireccionResponse(
        Long idDireccion,
        Long idUsuario,
        String calle,
        String numero,
        String comuna,
        String ciudad,
        String referencia
) {
    public static DireccionResponse desde(DireccionDespacho d) {
        return new DireccionResponse(d.getIdDireccion(), d.getIdUsuario(), d.getCalle(), d.getNumero(),
                d.getComuna(), d.getCiudad(), d.getReferencia());
    }
}
