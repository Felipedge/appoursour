package cl.oursour.ms_metricas.dto;

public record CombinacionResponse(
        Long idIngrediente1,
        String nombre1,
        Long idIngrediente2,
        String nombre2,
        Long cantidad
) {
}
