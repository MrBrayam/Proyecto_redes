package api.proyecto.redes.dto;

import java.math.BigDecimal;

public record ConductorRequest(
    Long usuarioId,
    String licencia,
    String vehiculo,
    BigDecimal calificacionPromedio,
    Boolean disponible
) {
}
