package api.proyecto.redes.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReporteAdminResumenResponse(
    String desde,
    String hasta,
    Long viajesTotales,
    Long viajesFinalizados,
    Long viajesCancelados,
    Long usuariosNuevos,
    BigDecimal ingresosBrutos,
    BigDecimal comisionesPlataforma,
    BigDecimal gananciasConductores,
    List<ReporteSeriePunto> serieViajes
) {
}
