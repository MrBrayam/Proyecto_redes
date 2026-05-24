package api.proyecto.redes.dto;

import java.util.List;

public record ReporteResumenResponse(
    String periodo,
    String etiqueta,
    Long viajes,
    Long usuarios,
    Long pasajeros,
    Long conductores,
    Long administradores,
    List<ReporteSeriePunto> serieViajes
) {
}
