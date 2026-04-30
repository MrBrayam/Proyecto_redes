package api.proyecto.redes.dto;

import java.time.LocalDateTime;

public record PasajeroResponse(
    Long idUsuario,
    String nombre,
    String email,
    LocalDateTime creadoEn
) {
}
