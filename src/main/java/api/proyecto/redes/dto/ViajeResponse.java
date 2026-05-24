package api.proyecto.redes.dto;

import api.proyecto.redes.model.EstadoViaje;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ViajeResponse(
    Long idViaje,
    Long pasajeroId,
    String pasajeroNombre,
    Long conductorId,
    String conductorNombre,
    BigDecimal origenLat,
    BigDecimal origenLng,
    BigDecimal destinoLat,
    BigDecimal destinoLng,
    EstadoViaje estado,
    BigDecimal precio,
    LocalDateTime creadoEn,
    LocalDateTime actualizadoEn
) {
}
