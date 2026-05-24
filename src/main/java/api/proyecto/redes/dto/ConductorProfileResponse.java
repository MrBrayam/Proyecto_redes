package api.proyecto.redes.dto;

import java.math.BigDecimal;

public record ConductorProfileResponse(
    Long idConductor,
    Long usuarioId,
    String nombre,
    String email,
    String licencia,
    String vehiculo,
    BigDecimal calificacionPromedio,
    Boolean disponible
) {
}
