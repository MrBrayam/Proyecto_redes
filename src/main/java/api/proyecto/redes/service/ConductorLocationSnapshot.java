package api.proyecto.redes.service;

import java.time.Instant;

public record ConductorLocationSnapshot(
    double lat,
    double lng,
    Instant actualizadoEn
) {
}
