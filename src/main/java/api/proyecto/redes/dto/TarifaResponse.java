package api.proyecto.redes.dto;

import java.math.BigDecimal;

public record TarifaResponse(
    Double distanciaKm,
    BigDecimal tarifaBase,
    BigDecimal multiplicadorDemanda,
    BigDecimal tarifaTotal,
    String descripcion
) {
}
