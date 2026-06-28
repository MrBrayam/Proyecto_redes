package api.proyecto.redes.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CancelacionResponse(
    Long idCancelacion,
    Long viajeId,
    String canceladoPor,
    String tipoCancelacion,
    String motivo,
    BigDecimal monto,
    LocalDateTime creadoEn
) {
}
