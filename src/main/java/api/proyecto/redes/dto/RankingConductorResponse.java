package api.proyecto.redes.dto;

import java.math.BigDecimal;

public record RankingConductorResponse(
    Long idConductor,
    String nombre,
    String vehiculo,
    BigDecimal calificacionPromedio,
    BigDecimal ganancias
) {
}
