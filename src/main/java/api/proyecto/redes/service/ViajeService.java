package api.proyecto.redes.service;

import api.proyecto.redes.dto.ViajeRequest;
import api.proyecto.redes.model.Calificacion;
import api.proyecto.redes.model.Cancelacion;
import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.model.EstadoViaje;
import api.proyecto.redes.model.TipoNotificacion;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.model.Viaje;
import api.proyecto.redes.repository.CalificacionRepository;
import api.proyecto.redes.repository.CancelacionRepository;
import api.proyecto.redes.repository.ViajeRepository;
import api.proyecto.redes.util.GeoUtils;
import api.proyecto.redes.util.TarifaCalculadora;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ViajeService {

    private final ViajeRepository viajeRepository;
    private final CalificacionRepository calificacionRepository;
    private final CancelacionRepository cancelacionRepository;
    private final NotificacionService notificacionService;

    public ViajeService(ViajeRepository viajeRepository,
                        CalificacionRepository calificacionRepository,
                        CancelacionRepository cancelacionRepository,
                        NotificacionService notificacionService) {
        this.viajeRepository = viajeRepository;
        this.calificacionRepository = calificacionRepository;
        this.cancelacionRepository = cancelacionRepository;
        this.notificacionService = notificacionService;
    }

    // ==================== FASE 1: Conductores y Tarifa ====================

    /**
     * Calcula la tarifa estimada basada en coordenadas de origen y destino
     */
    public BigDecimal calcularTarifaEstimada(BigDecimal origenLat, BigDecimal origenLng,
                                             BigDecimal destinoLat, BigDecimal destinoLng,
                                             BigDecimal multiplicadorDemanda) {
        double distanciaKm = GeoUtils.calcularDistanciaKm(origenLat, origenLng, destinoLat, destinoLng);
        return TarifaCalculadora.calcularTarifaConDemanda(distanciaKm, 
            multiplicadorDemanda != null ? multiplicadorDemanda : BigDecimal.ONE);
    }

    // ==================== FASE 2: Cancelaciones ====================

    /**
     * Cancela un viaje y aplica multa si corresponde
     */
    public Cancelacion cancelarViaje(Long viajeId, Usuario usuario, String motivo) {
        Viaje viaje = obtenerPorId(viajeId);
        
        // Solo puede cancelarse en estados SOLICITADO o ACEPTADO
        if (viaje.getEstado() != EstadoViaje.SOLICITADO && 
            viaje.getEstado() != EstadoViaje.ACEPTADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El viaje no puede ser cancelado en este estado");
        }

        // Determinar si es pasajero o conductor
        boolean esPasajero = viaje.getPasajero().getIdUsuario().equals(usuario.getIdUsuario());
        boolean esConductor = viaje.getConductor() != null && 
                              viaje.getConductor().getUsuario().getIdUsuario().equals(usuario.getIdUsuario());

        if (!esPasajero && !esConductor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para cancelar este viaje");
        }

        // Calcular multa
        BigDecimal multa = TarifaCalculadora.calcularMultaCancelacion(viaje.getPrecio(), esPasajero);

        // Crear cancelación
        Cancelacion.TipoCancelacion tipo = esPasajero ? Cancelacion.TipoCancelacion.PASAJERO : Cancelacion.TipoCancelacion.CONDUCTOR;
        Cancelacion cancelacion = new Cancelacion(viaje, usuario, tipo, motivo, multa);
        cancelacionRepository.save(cancelacion);

        // Cambiar estado del viaje
        viaje.setEstado(EstadoViaje.CANCELADO);
        viajeRepository.save(viaje);

        // Enviar notificaciones
        String motivoText = motivo != null ? motivo : "Sin especificar";
        
        if (esPasajero) {
            // Notificar al conductor si existe
            if (viaje.getConductor() != null) {
                notificacionService.enviarNotificacionViaje(
                    viaje.getConductor().getUsuario().getIdUsuario(),
                    "Viaje cancelado",
                    "El pasajero canceló el viaje. Motivo: " + motivoText,
                    TipoNotificacion.VIAJE_CANCELADO,
                    viaje.getIdViaje()
                );
            }
        } else {
            // Notificar al pasajero
            notificacionService.enviarNotificacionViaje(
                viaje.getPasajero().getIdUsuario(),
                "Viaje cancelado",
                "El conductor canceló el viaje. Motivo: " + motivoText,
                TipoNotificacion.VIAJE_CANCELADO,
                viaje.getIdViaje()
            );
        }

        return cancelacion;
    }

    // ==================== FASE 3: Calificaciones ====================

    /**
     * Califica un viaje finalizado
     */
    public Calificacion calificarViaje(Long viajeId, Usuario calificador, Integer puntuacion, String comentario) {
        if (puntuacion == null || puntuacion < 1 || puntuacion > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La puntuación debe estar entre 1 y 5");
        }

        Viaje viaje = obtenerPorId(viajeId);

        if (viaje.getEstado() != EstadoViaje.FINALIZADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo pueden calificarse viajes finalizados");
        }

        // Verificar que el calificador sea pasajero o conductor del viaje
        boolean esPasajero = viaje.getPasajero().getIdUsuario().equals(calificador.getIdUsuario());
        boolean esConductor = viaje.getConductor() != null && 
                              viaje.getConductor().getUsuario().getIdUsuario().equals(calificador.getIdUsuario());

        if (!esPasajero && !esConductor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para calificar este viaje");
        }

        // Crear calificación
        Calificacion calificacion = new Calificacion(viaje, calificador, puntuacion, comentario);
        return calificacionRepository.save(calificacion);
    }

    /**
     * Obtiene las calificaciones de un conductor
     */
    public List<Calificacion> obtenerCalificacionesConductor(Long conductorId) {
        return calificacionRepository.findCalificacionesPorConductor(conductorId);
    }

    /**
     * Calcula el rating promedio de un conductor
     */
    public Double calcularRatingPromedioConductor(Long conductorId) {
        Double promedio = calificacionRepository.calcularCalificacionPromedioPorConductor(conductorId);
        return promedio != null ? promedio : 0.0;
    }

    // ==================== FASE 4: Historial ====================

    /**
     * Obtiene el historial de viajes de un pasajero
     */
    public List<Viaje> obtenerHistorialPasajero(Long usuarioId) {
        return viajeRepository.obtenerHistorialPasajero(usuarioId);
    }

    /**
     * Obtiene el historial de viajes de un conductor
     */
    public List<Viaje> obtenerHistorialConductor(Long conductorId) {
        return viajeRepository.obtenerHistorialConductor(conductorId);
    }

    /**
     * Obtiene información de ganancias del conductor
     */
    public BigDecimal obtenerGananciasTotalConductor(Long conductorId) {
        Double ganancias = viajeRepository.sumGananciasConductor(conductorId, EstadoViaje.FINALIZADO);
        return ganancias != null ? BigDecimal.valueOf(ganancias) : BigDecimal.ZERO;
    }

    // ==================== Métodos existentes (refactorizados) ====================

    public Viaje crearSolicitud(Usuario pasajero, ViajeRequest request) {
        return crearSolicitud(pasajero, request, null);
    }

    public Viaje crearSolicitud(Usuario pasajero, ViajeRequest request, Conductor conductorAsignado) {
        if (pasajero == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pasajero requerido");
        }
        if (request == null || request.origenLat() == null || request.origenLng() == null
            || request.destinoLat() == null || request.destinoLng() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origen y destino son requeridos");
        }

        // Calcular distancia y tarifa
        double distanciaKm = GeoUtils.calcularDistanciaKm(request.origenLat(), request.origenLng(),
                                                          request.destinoLat(), request.destinoLng());
        BigDecimal tarifaBase = TarifaCalculadora.calcularTarifaBase(distanciaKm);

        Viaje viaje = new Viaje();
        viaje.setPasajero(pasajero);
        viaje.setOrigenLat(request.origenLat());
        viaje.setOrigenLng(request.origenLng());
        viaje.setDestinoLat(request.destinoLat());
        viaje.setDestinoLng(request.destinoLng());
        viaje.setDistanciaKm(BigDecimal.valueOf(distanciaKm));
        viaje.setPrecioBase(tarifaBase);
        viaje.setPrecio(tarifaBase);
        viaje.setMultiplicadorDemanda(BigDecimal.ONE);
        
        if (conductorAsignado != null) {
            viaje.setConductor(conductorAsignado);
        }
        viaje.setEstado(EstadoViaje.SOLICITADO);
        return viajeRepository.save(viaje);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<Viaje> listarPendientes() {
        return viajeRepository.findByEstado(EstadoViaje.SOLICITADO);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<java.util.Map<String, Object>> listarPendientesPayload() {
        List<Viaje> pendientes = viajeRepository.findByEstado(EstadoViaje.SOLICITADO);
        return pendientes.stream().map(v -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("viajeId", v.getIdViaje());
            map.put("pasajeroId", v.getPasajero().getIdUsuario());
            map.put("pasajeroNombre", v.getPasajero().getNombre());
            map.put("origenLat", v.getOrigenLat());
            map.put("origenLng", v.getOrigenLng());
            map.put("destinoLat", v.getDestinoLat());
            map.put("destinoLng", v.getDestinoLng());
            map.put("estado", v.getEstado().name());
            return map;
        }).toList();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Long validarYObtenerPasajeroIdDeViaje(Long viajeId, Long conductorUsuarioId) {
        Viaje viaje = viajeRepository.findById(viajeId).orElse(null);
        if (viaje == null || viaje.getConductor() == null) {
            return null;
        }
        if (!viaje.getConductor().getUsuario().getIdUsuario().equals(conductorUsuarioId)) {
            return null;
        }
        return viaje.getPasajero().getIdUsuario();
    }

    public Viaje obtenerPorId(Long id) {
        return viajeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Viaje no encontrado"));
    }

    public Viaje aceptarViaje(Long id, Conductor conductor) {
        Viaje viaje = obtenerPorId(id);
        if (viaje.getEstado() != EstadoViaje.SOLICITADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El viaje ya no esta disponible");
        }
        viaje.setConductor(conductor);
        viaje.setEstado(EstadoViaje.ACEPTADO);
        Viaje viajeGuardado = viajeRepository.save(viaje);

        // Enviar notificación al pasajero
        notificacionService.enviarNotificacionViaje(
            viaje.getPasajero().getIdUsuario(),
            "¡Conductor asignado!",
            "El conductor " + conductor.getUsuario().getNombre() + " ha aceptado tu viaje. Vehículo: " + conductor.getVehiculo(),
            TipoNotificacion.VIAJE_ACEPTADO,
            viaje.getIdViaje()
        );

        return viajeGuardado;
    }

    public Viaje rechazarViaje(Long id) {
        Viaje viaje = obtenerPorId(id);
        if (viaje.getEstado() != EstadoViaje.SOLICITADO) {
            return viaje;
        }
        viaje.setEstado(EstadoViaje.RECHAZADO);
        return viajeRepository.save(viaje);
    }

    public Viaje finalizarViaje(Long id) {
        Viaje viaje = obtenerPorId(id);
        viaje.setEstado(EstadoViaje.FINALIZADO);
        Viaje viajeGuardado = viajeRepository.save(viaje);

        // Enviar notificación al pasajero
        notificacionService.enviarNotificacionViaje(
            viaje.getPasajero().getIdUsuario(),
            "Viaje completado",
            "Tu viaje ha finalizado. Tarifa: S/. " + viaje.getPrecio(),
            TipoNotificacion.VIAJE_FINALIZADO,
            viaje.getIdViaje()
        );

        // Enviar notificación al conductor
        if (viaje.getConductor() != null) {
            BigDecimal gananciaConductor = TarifaCalculadora.calcularGananciaConductor(viaje.getPrecio());
            notificacionService.enviarNotificacionViaje(
                viaje.getConductor().getUsuario().getIdUsuario(),
                "Viaje completado",
                "Ganancia: S/. " + gananciaConductor,
                TipoNotificacion.VIAJE_FINALIZADO,
                viaje.getIdViaje()
            );
        }

        return viajeGuardado;
    }

    public List<Viaje> listarPorPasajero(Long usuarioId) {
        return viajeRepository.findByPasajero_IdUsuario(usuarioId);
    }

    public List<Viaje> listarPorConductor(Long conductorId) {
        return viajeRepository.findByConductor_IdConductor(conductorId);
    }

    public double obtenerDistancia(BigDecimal origenLat, BigDecimal origenLng,
                                   BigDecimal destinoLat, BigDecimal destinoLng) {
        return GeoUtils.calcularDistanciaKm(origenLat, origenLng, destinoLat, destinoLng);
    }
}

