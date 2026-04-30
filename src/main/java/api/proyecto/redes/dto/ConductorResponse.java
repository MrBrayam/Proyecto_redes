package api.proyecto.redes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConductorResponse(
    Long idConductor,
    Long usuarioId,
    String licencia,
    String vehiculo,
    BigDecimal calificacionPromedio,
    Boolean disponible,
    LocalDateTime creadoEn
) {
}
