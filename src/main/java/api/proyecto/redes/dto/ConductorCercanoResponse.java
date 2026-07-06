package api.proyecto.redes.dto;

import java.math.BigDecimal;

public record ConductorCercanoResponse(
    Long idConductor,
    String nombre,
    String vehiculo,
    BigDecimal calificacionPromedio,
    boolean disponible,
    double distanciaKm,
    Double lat,
    Double lng
) {
}
