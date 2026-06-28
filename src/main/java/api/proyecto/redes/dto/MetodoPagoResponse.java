package api.proyecto.redes.dto;

import api.proyecto.redes.model.TipoMetodoPago;
import java.time.LocalDateTime;

public record MetodoPagoResponse(
    Long idMetodoPago,
    TipoMetodoPago tipo,
    String ultimosCuatroDigitos,
    String nombreTitular,
    Boolean predeterminado,
    LocalDateTime creadoEn
) {}
