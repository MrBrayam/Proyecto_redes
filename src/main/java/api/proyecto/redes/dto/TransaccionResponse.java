package api.proyecto.redes.dto;

import api.proyecto.redes.model.EstadoTransaccion;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransaccionResponse(
    Long idTransaccion,
    Long viajeId,
    BigDecimal monto,
    BigDecimal comision,
    BigDecimal gananciaConductor,
    EstadoTransaccion estado,
    String metodoPago,
    String descripcion,
    String razonFallo,
    LocalDateTime creadoEn,
    LocalDateTime completadoEn
) {}
