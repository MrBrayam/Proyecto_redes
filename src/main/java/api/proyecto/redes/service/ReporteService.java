package api.proyecto.redes.service;

import api.proyecto.redes.dto.ReporteResumenResponse;
import api.proyecto.redes.dto.ReporteSeriePunto;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.repository.SerieConteoRow;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.repository.ViajeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class ReporteService {

    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;

    public ReporteService(ViajeRepository viajeRepository, UsuarioRepository usuarioRepository) {
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ReporteResumenResponse reporteDia(LocalDate fecha) {
        LocalDateTime desde = fecha.atStartOfDay();
        LocalDateTime hasta = fecha.plusDays(1).atStartOfDay();

        long viajes = viajeRepository.countByCreadoEnBetween(desde, hasta);
        long usuarios = usuarioRepository.countByCreadoEnBetween(desde, hasta);
        long pasajeros = usuarioRepository.countByRolAndCreadoEnBetween(RolUsuario.PASAJERO, desde, hasta);
        long conductores = usuarioRepository.countByRolAndCreadoEnBetween(RolUsuario.CONDUCTOR, desde, hasta);
        long administradores = usuarioRepository.countByRolAndCreadoEnBetween(RolUsuario.ADMIN, desde, hasta);

        List<ReporteSeriePunto> serie = List.of(new ReporteSeriePunto(fecha.toString(), viajes));
        return new ReporteResumenResponse("dia", fecha.toString(), viajes, usuarios, pasajeros, conductores, administradores, serie);
    }

    public ReporteResumenResponse reporteMes(int anio, int mes) {
        YearMonth yearMonth = YearMonth.of(anio, mes);
        LocalDateTime desde = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime hasta = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

        long viajes = viajeRepository.countByCreadoEnBetween(desde, hasta);
        long usuarios = usuarioRepository.countByCreadoEnBetween(desde, hasta);
        long pasajeros = usuarioRepository.countByRolAndCreadoEnBetween(RolUsuario.PASAJERO, desde, hasta);
        long conductores = usuarioRepository.countByRolAndCreadoEnBetween(RolUsuario.CONDUCTOR, desde, hasta);
        long administradores = usuarioRepository.countByRolAndCreadoEnBetween(RolUsuario.ADMIN, desde, hasta);

        List<ReporteSeriePunto> serie = viajeRepository.conteoPorDia(desde, hasta).stream()
            .map(this::toSerie)
            .toList();

        String etiqueta = yearMonth.toString();
        return new ReporteResumenResponse("mes", etiqueta, viajes, usuarios, pasajeros, conductores, administradores, serie);
    }

    public ReporteResumenResponse reporteAnio(int anio) {
        LocalDateTime desde = LocalDate.of(anio, 1, 1).atStartOfDay();
        LocalDateTime hasta = LocalDate.of(anio + 1, 1, 1).atStartOfDay();

        long viajes = viajeRepository.countByCreadoEnBetween(desde, hasta);
        long usuarios = usuarioRepository.countByCreadoEnBetween(desde, hasta);
        long pasajeros = usuarioRepository.countByRolAndCreadoEnBetween(RolUsuario.PASAJERO, desde, hasta);
        long conductores = usuarioRepository.countByRolAndCreadoEnBetween(RolUsuario.CONDUCTOR, desde, hasta);
        long administradores = usuarioRepository.countByRolAndCreadoEnBetween(RolUsuario.ADMIN, desde, hasta);

        List<ReporteSeriePunto> serie = viajeRepository.conteoPorMes(anio).stream()
            .map(this::toSerie)
            .toList();

        return new ReporteResumenResponse("anio", String.valueOf(anio), viajes, usuarios, pasajeros, conductores, administradores, serie);
    }

    private ReporteSeriePunto toSerie(SerieConteoRow row) {
        return new ReporteSeriePunto(row.getEtiqueta(), row.getTotal());
    }
}
