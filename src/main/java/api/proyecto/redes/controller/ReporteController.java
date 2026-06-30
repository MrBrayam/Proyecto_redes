package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.RankingConductorResponse;
import api.proyecto.redes.dto.ReporteAdminResumenResponse;
import api.proyecto.redes.dto.ReporteResumenResponse;
import api.proyecto.redes.dto.ReporteSeriePunto;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.ReporteService;
import api.proyecto.redes.util.AuthTokenExtractor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final AuthService authService;

    public ReporteController(ReporteService reporteService, AuthService authService) {
        this.reporteService = reporteService;
        this.authService = authService;
    }

    @GetMapping("/dia")
    public ReporteResumenResponse reporteDia(
        @RequestParam(value = "fecha", required = false) String fecha,
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        validarAdmin(authorization, tokenHeader);
        LocalDate target = fecha == null || fecha.isBlank() ? LocalDate.now() : LocalDate.parse(fecha);
        return reporteService.reporteDia(target);
    }

    @GetMapping("/mes")
    public ReporteResumenResponse reporteMes(
        @RequestParam(value = "anio", required = false) Integer anio,
        @RequestParam(value = "mes", required = false) Integer mes,
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        validarAdmin(authorization, tokenHeader);
        YearMonth now = YearMonth.now();
        int targetAnio = anio == null ? now.getYear() : anio;
        int targetMes = mes == null ? now.getMonthValue() : mes;
        return reporteService.reporteMes(targetAnio, targetMes);
    }

    @GetMapping("/anio")
    public ReporteResumenResponse reporteAnio(
        @RequestParam(value = "anio", required = false) Integer anio,
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        validarAdmin(authorization, tokenHeader);
        int target = anio == null ? LocalDate.now().getYear() : anio;
        return reporteService.reporteAnio(target);
    }

    @GetMapping("/resumen")
    public ReporteAdminResumenResponse resumenRango(
        @RequestParam("desde") String desde,
        @RequestParam("hasta") String hasta,
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        validarAdmin(authorization, tokenHeader);
        LocalDate fechaDesde = LocalDate.parse(desde);
        LocalDate fechaHasta = LocalDate.parse(hasta);
        return reporteService.reporteRango(fechaDesde, fechaHasta);
    }

    @GetMapping("/conductores/ranking")
    public List<RankingConductorResponse> rankingConductores(
        @RequestParam(value = "desde", required = false) String desde,
        @RequestParam(value = "hasta", required = false) String hasta,
        @RequestParam(value = "limite", required = false) Integer limite,
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        validarAdmin(authorization, tokenHeader);
        LocalDate fechaDesde = (desde == null || desde.isBlank()) ? null : LocalDate.parse(desde);
        LocalDate fechaHasta = (hasta == null || hasta.isBlank()) ? null : LocalDate.parse(hasta);
        return reporteService.rankingConductores(fechaDesde, fechaHasta, limite == null ? 10 : limite);
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportar(
        @RequestParam("periodo") String periodo,
        @RequestParam(value = "fecha", required = false) String fecha,
        @RequestParam(value = "anio", required = false) Integer anio,
        @RequestParam(value = "mes", required = false) Integer mes,
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        validarAdmin(authorization, tokenHeader);

        ReporteResumenResponse reporte;
        switch (periodo) {
            case "dia" -> {
                LocalDate target = fecha == null || fecha.isBlank() ? LocalDate.now() : LocalDate.parse(fecha);
                reporte = reporteService.reporteDia(target);
            }
            case "mes" -> {
                YearMonth now = YearMonth.now();
                int targetAnio = anio == null ? now.getYear() : anio;
                int targetMes = mes == null ? now.getMonthValue() : mes;
                reporte = reporteService.reporteMes(targetAnio, targetMes);
            }
            case "anio" -> {
                int target = anio == null ? LocalDate.now().getYear() : anio;
                reporte = reporteService.reporteAnio(target);
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Periodo invalido");
        }

        StringBuilder csv = new StringBuilder();
        csv.append("periodo,etiqueta,viajes,usuarios,pasajeros,conductores,administradores\n");
        csv.append(String.join(",",
            reporte.periodo(),
            reporte.etiqueta(),
            String.valueOf(reporte.viajes()),
            String.valueOf(reporte.usuarios()),
            String.valueOf(reporte.pasajeros()),
            String.valueOf(reporte.conductores()),
            String.valueOf(reporte.administradores())
        ));
        csv.append("\n\nserie_etiqueta,serie_total\n");
        for (ReporteSeriePunto punto : reporte.serieViajes()) {
            csv.append(punto.etiqueta()).append(",").append(punto.total()).append("\n");
        }

        return ResponseEntity.ok()
            .contentType(new MediaType("text", "csv"))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reportes-" + reporte.periodo() + ".csv")
            .body(csv.toString());
    }

    private void validarAdmin(String authorization, String tokenHeader) {
        String token = AuthTokenExtractor.extraerToken(authorization, tokenHeader);
        AuthResponse session = authService.obtenerSesion(token);
        if (session.usuario() == null || session.usuario().rol() != RolUsuario.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
        }
    }
}
