package api.proyecto.redes.dto;

import java.time.LocalDateTime;

public record CalificacionResponse(
    Long idCalificacion,
    String nombreCalificador,
    Integer puntuacion,
    String comentario,
    LocalDateTime creadoEn
) {
}
