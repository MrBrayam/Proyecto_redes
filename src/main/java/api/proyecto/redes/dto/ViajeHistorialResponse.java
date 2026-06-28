package api.proyecto.redes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ViajeHistorialResponse(
    Long viajeId,
    String nombreOtroUsuario,
    String estado,
    BigDecimal origenLat,
    BigDecimal origenLng,
    BigDecimal destinoLat,
    BigDecimal destinoLng,
    BigDecimal precio,
    BigDecimal distanciaKm,
    Integer calificacion,
    String comentarioCalificacion,
    LocalDateTime creadoEn,
    LocalDateTime actualizadoEn
) {
}
