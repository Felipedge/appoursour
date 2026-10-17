package cl.oursour.ms_metricas.dto;

import java.time.LocalDate;
import java.util.List;

public record TendenciaResponse(
        LocalDate desde,
        LocalDate hasta,
        long totalBusquedas,
        List<Dia> busquedasPorDia,
        List<ConteoResponse> ingredientesDelPeriodo
) {
    public record Dia(
            LocalDate fecha,
            long busquedas
    ) {
    }
}
