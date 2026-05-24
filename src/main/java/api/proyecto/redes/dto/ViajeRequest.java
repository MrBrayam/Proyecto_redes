package api.proyecto.redes.dto;

import java.math.BigDecimal;

public record ViajeRequest(
    BigDecimal origenLat,
    BigDecimal origenLng,
    BigDecimal destinoLat,
    BigDecimal destinoLng,
    String destinoTexto
) {
}
