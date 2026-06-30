package api.proyecto.redes.service;

import api.proyecto.redes.dto.ReporteResumenResponse;
import api.proyecto.redes.dto.ReporteSeriePunto;
import api.proyecto.redes.dto.RankingConductorResponse;
import api.proyecto.redes.dto.ReporteAdminResumenResponse;
import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.model.EstadoTransaccion;
import api.proyecto.redes.model.EstadoViaje;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.repository.SerieConteoRow;
import api.proyecto.redes.repository.ConductorRepository;
import api.proyecto.redes.repository.TransaccionRepository;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.repository.ViajeRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class ReporteService {

    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransaccionRepository transaccionRepository;
    private final ConductorRepository conductorRepository;

    public ReporteService(ViajeRepository viajeRepository,
                          UsuarioRepository usuarioRepository,
                          TransaccionRepository transaccionRepository,
                          ConductorRepository conductorRepository) {
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.transaccionRepository = transaccionRepository;
        this.conductorRepository = conductorRepository;
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

    public ReporteAdminResumenResponse reporteRango(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Rango de fechas requerido");
        }
        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha hasta no puede ser menor que desde");
        }

        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay();

        long viajesTotales = viajeRepository.countByCreadoEnBetween(inicio, fin);
        long viajesFinalizados = viajeRepository.countByEstadoYFecha(EstadoViaje.FINALIZADO, inicio, fin);
        long viajesCancelados = viajeRepository.countByEstadoYFecha(EstadoViaje.CANCELADO, inicio, fin);
        long usuariosNuevos = usuarioRepository.countByCreadoEnBetween(inicio, fin);

        BigDecimal ingresos = transaccionRepository.sumMontoByEstadoYRango(EstadoTransaccion.COMPLETADA, inicio, fin);
        BigDecimal comisiones = transaccionRepository.sumComisionByEstadoYRango(EstadoTransaccion.COMPLETADA, inicio, fin);
        BigDecimal gananciasConductores = transaccionRepository.sumGananciaConductorByEstadoYRango(EstadoTransaccion.COMPLETADA, inicio, fin);

        List<ReporteSeriePunto> serie = viajeRepository.conteoPorDia(inicio, fin).stream()
            .map(this::toSerie)
            .toList();

        return new ReporteAdminResumenResponse(
            desde.toString(),
            hasta.toString(),
            viajesTotales,
            viajesFinalizados,
            viajesCancelados,
            usuariosNuevos,
            ingresos,
            comisiones,
            gananciasConductores,
            serie
        );
    }

    public List<RankingConductorResponse> rankingConductores(LocalDate desde, LocalDate hasta, int limite) {
        int top = limite <= 0 ? 10 : Math.min(limite, 50);

        LocalDateTime inicio = (desde == null ? LocalDate.now().minusDays(30) : desde).atStartOfDay();
        LocalDateTime fin = (hasta == null ? LocalDate.now() : hasta).plusDays(1).atStartOfDay();

        var transacciones = transaccionRepository.findByEstadoAndCompletadoEnBetween(EstadoTransaccion.COMPLETADA, inicio, fin);
        Map<Long, BigDecimal> acumuladoGanancias = new HashMap<>();

        for (var transaccion : transacciones) {
            if (transaccion.getViaje() == null || transaccion.getViaje().getConductor() == null) {
                continue;
            }
            Long conductorId = transaccion.getViaje().getConductor().getIdConductor();
            BigDecimal actual = acumuladoGanancias.getOrDefault(conductorId, BigDecimal.ZERO);
            acumuladoGanancias.put(conductorId, actual.add(transaccion.getGananciaConductor()));
        }

        return acumuladoGanancias.entrySet().stream()
            .map(entry -> toRankingResponse(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(RankingConductorResponse::ganancias).reversed())
            .limit(top)
            .toList();
    }

    private ReporteSeriePunto toSerie(SerieConteoRow row) {
        return new ReporteSeriePunto(row.getEtiqueta(), row.getTotal());
    }

    private RankingConductorResponse toRankingResponse(Long conductorId, BigDecimal ganancias) {
        Conductor conductor = conductorRepository.findById(conductorId).orElse(null);
        String nombre = "Conductor";
        String vehiculo = "-";
        BigDecimal calificacion = BigDecimal.ZERO;

        if (conductor != null) {
            if (conductor.getUsuario() != null && conductor.getUsuario().getNombre() != null) {
                nombre = conductor.getUsuario().getNombre();
            }
            if (conductor.getVehiculo() != null) {
                vehiculo = conductor.getVehiculo();
            }
            if (conductor.getCalificacionPromedio() != null) {
                calificacion = conductor.getCalificacionPromedio();
            }
        }

        return new RankingConductorResponse(
            conductorId,
            nombre,
            vehiculo,
            calificacion,
            ganancias
        );
    }
}
