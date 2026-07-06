package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.CalificacionRequest;
import api.proyecto.redes.dto.CalificacionResponse;
import api.proyecto.redes.dto.CancelacionRequest;
import api.proyecto.redes.dto.CancelacionResponse;
import api.proyecto.redes.dto.TarifaResponse;
import api.proyecto.redes.dto.ViajeRequest;
import api.proyecto.redes.dto.ViajeResponse;
import api.proyecto.redes.model.Calificacion;
import api.proyecto.redes.model.Cancelacion;
import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.model.EstadoViaje;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.model.Viaje;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.ConductorService;
import api.proyecto.redes.service.RideRealtimeService;
import api.proyecto.redes.service.ViajeService;
import api.proyecto.redes.util.AuthTokenExtractor;
import api.proyecto.redes.util.TarifaCalculadora;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/viajes")
public class ViajeController {

    private final ViajeService viajeService;
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final ConductorService conductorService;
    private final RideRealtimeService rideRealtimeService;

    public ViajeController(ViajeService viajeService,
                           AuthService authService,
                           UsuarioRepository usuarioRepository,
                           ConductorService conductorService,
                           RideRealtimeService rideRealtimeService) {
        this.viajeService = viajeService;
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
        this.conductorService = conductorService;
        this.rideRealtimeService = rideRealtimeService;
    }

    // ==================== FASE 1: Cálculo de Tarifa ====================

    /**
     * Calcula la tarifa estimada antes de crear el viaje
     */
    @PostMapping("/calcular-tarifa")
    public TarifaResponse calcularTarifa(@RequestBody ViajeRequest request,
                                         @RequestParam(value = "multiplicador", required = false) BigDecimal multiplicador) {
        if (request.origenLat() == null || request.origenLng() == null ||
            request.destinoLat() == null || request.destinoLng() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origen y destino requeridos");
        }

        BigDecimal tarifa = viajeService.calcularTarifaEstimada(
            request.origenLat(), request.origenLng(),
            request.destinoLat(), request.destinoLng(),
            multiplicador);

        double distancia = viajeService.obtenerDistancia(request.origenLat(), request.origenLng(),
                                                         request.destinoLat(), request.destinoLng());

        BigDecimal mult = multiplicador != null ? multiplicador : BigDecimal.ONE;
        BigDecimal tarifaBase = TarifaCalculadora.calcularTarifaBase(distancia);

        return new TarifaResponse(
            distancia,
            tarifaBase,
            mult,
            tarifa,
            "Tarifa estimada (puede variar según demanda)"
        );
    }

    // ==================== Creación de Viajes ====================

    @PostMapping
    public ViajeResponse crear(@RequestBody ViajeRequest request,
                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                              @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRol(authorization, tokenHeader, RolUsuario.PASAJERO);
        Usuario pasajero = usuarioRepository.findById(session.usuario().idUsuario())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pasajero no encontrado"));
        Conductor conductor = null;
        if (request.conductorId() != null) {
            conductor = conductorService.obtenerPorId(request.conductorId());
        }
        Viaje viaje = viajeService.crearSolicitud(pasajero, request, conductor);
        if (conductor != null) {
            rideRealtimeService.enviarAConductor(conductor.getIdConductor(), Map.of(
                "type", "ride-request",
                "viajeId", viaje.getIdViaje(),
                "pasajeroId", pasajero.getIdUsuario(),
                "pasajeroNombre", pasajero.getNombre(),
                "origenLat", viaje.getOrigenLat(),
                "origenLng", viaje.getOrigenLng(),
                "destinoLat", viaje.getDestinoLat(),
                "destinoLng", viaje.getDestinoLng(),
                "destino", request.destinoTexto()
            ));
        } else {
            rideRealtimeService.broadcastConductores(Map.of(
                "type", "ride-request",
                "viajeId", viaje.getIdViaje(),
                "pasajeroId", pasajero.getIdUsuario(),
                "pasajeroNombre", pasajero.getNombre(),
                "origenLat", viaje.getOrigenLat(),
                "origenLng", viaje.getOrigenLng(),
                "destinoLat", viaje.getDestinoLat(),
                "destinoLng", viaje.getDestinoLng(),
                "destino", request.destinoTexto()
            ));
        }
        return toResponse(viaje);
    }

    // ==================== Listar Viajes ====================

    @GetMapping("/pasajero")
    public List<ViajeResponse> listarPorPasajero(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRol(authorization, tokenHeader, RolUsuario.PASAJERO);
        return viajeService.listarPorPasajero(session.usuario().idUsuario()).stream().map(this::toResponse).toList();
    }

    @GetMapping("/pendientes")
    public List<ViajeResponse> listarPendientes(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        validarRol(authorization, tokenHeader, RolUsuario.CONDUCTOR);
        return viajeService.listarPendientes().stream().map(this::toResponse).toList();
    }

    // ==================== FASE 4: Historial ====================

    /**
     * Obtiene el historial de viajes del pasajero autenticado
     */
    @GetMapping("/historial/pasajero")
    public List<ViajeResponse> obtenerHistorialPasajero(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRol(authorization, tokenHeader, RolUsuario.PASAJERO);
        return viajeService.obtenerHistorialPasajero(session.usuario().idUsuario())
            .stream().map(this::toResponse).toList();
    }

    /**
     * Obtiene el historial de viajes del conductor autenticado
     */
    @GetMapping("/historial/conductor")
    public List<ViajeResponse> obtenerHistorialConductor(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRol(authorization, tokenHeader, RolUsuario.CONDUCTOR);
        Conductor conductor = conductorService.obtenerPorUsuarioId(session.usuario().idUsuario());
        return viajeService.obtenerHistorialConductor(conductor.getIdConductor())
            .stream().map(this::toResponse).toList();
    }

    // ==================== Acciones de Viaje ====================

    @GetMapping("/{id}")
    public ViajeResponse obtener(@PathVariable Long id,
                                 @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                 @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRolMixto(authorization, tokenHeader);
        Viaje viaje = viajeService.obtenerPorId(id);
        if (session.usuario().rol() == RolUsuario.PASAJERO
            && !session.usuario().idUsuario().equals(viaje.getPasajero().getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin acceso al viaje");
        }
        return toResponse(viaje);
    }

    @PostMapping("/{id}/aceptar")
    public ViajeResponse aceptar(@PathVariable Long id,
                                 @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                 @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRol(authorization, tokenHeader, RolUsuario.CONDUCTOR);
        Conductor conductor = conductorService.obtenerPorUsuarioId(session.usuario().idUsuario());
        Viaje viaje = viajeService.aceptarViaje(id, conductor);

        // Notify passenger in real time via WebSocket!
        rideRealtimeService.enviarAPasajero(viaje.getPasajero().getIdUsuario(), Map.of(
            "type", "ride-status",
            "status", EstadoViaje.ACEPTADO.name(),
            "viajeId", viaje.getIdViaje(),
            "conductorNombre", session.usuario().nombre()
        ));

        // Broadcast to other drivers to remove it from their queues
        rideRealtimeService.broadcastConductores(Map.of(
            "type", "ride-taken",
            "viajeId", viaje.getIdViaje()
        ));

        return toResponse(viaje);
    }

    @PostMapping("/{id}/rechazar")
    public ViajeResponse rechazar(@PathVariable Long id,
                                  @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                  @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRol(authorization, tokenHeader, RolUsuario.CONDUCTOR);
        Viaje viaje = viajeService.rechazarViaje(id);

        // Notify passenger in real time via WebSocket!
        rideRealtimeService.enviarAPasajero(viaje.getPasajero().getIdUsuario(), Map.of(
            "type", "ride-status",
            "status", "RECHAZADO",
            "viajeId", viaje.getIdViaje()
        ));

        return toResponse(viaje);
    }

    // ==================== FASE 2: Cancelaciones ====================

    /**
     * Cancela un viaje
     */
    @PostMapping("/{id}/cancelar")
    public CancelacionResponse cancelarViaje(@PathVariable Long id,
                                            @RequestBody(required = false) CancelacionRequest request,
                                            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                            @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRolMixto(authorization, tokenHeader);
        Usuario usuario = usuarioRepository.findById(session.usuario().idUsuario())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String motivo = request != null && request.motivo() != null ? request.motivo() : "Sin especificar";

        Cancelacion cancelacion = viajeService.cancelarViaje(id, usuario, motivo);

        return new CancelacionResponse(
            cancelacion.getIdCancelacion(),
            cancelacion.getViaje().getIdViaje(),
            cancelacion.getCanceladoPor().getNombre(),
            cancelacion.getTipoCancelacion().toString(),
            cancelacion.getMotivo(),
            cancelacion.getMonto(),
            cancelacion.getCreadoEn()
        );
    }

    @PostMapping("/{id}/finalizar")
    public ViajeResponse finalizar(@PathVariable Long id,
                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                   @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRolMixto(authorization, tokenHeader);
        if (session.usuario().rol() != RolUsuario.CONDUCTOR && session.usuario().rol() != RolUsuario.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
        }
        Viaje viaje = viajeService.finalizarViaje(id);
        return toResponse(viaje);
    }

    // ==================== FASE 3: Calificaciones ====================

    /**
     * Califica un viaje completado
     */
    @PostMapping("/{id}/calificar")
    public CalificacionResponse calificarViaje(@PathVariable Long id,
                                              @RequestBody CalificacionRequest request,
                                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                              @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRolMixto(authorization, tokenHeader);
        Usuario usuario = usuarioRepository.findById(session.usuario().idUsuario())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Calificacion calificacion = viajeService.calificarViaje(id, usuario, request.puntuacion(), request.comentario());

        return new CalificacionResponse(
            calificacion.getIdCalificacion(),
            calificacion.getCalificador().getNombre(),
            calificacion.getPuntuacion(),
            calificacion.getComentario(),
            calificacion.getCreadoEn()
        );
    }

    /**
     * Obtiene las calificaciones de un conductor
     */
    @GetMapping("/conductor/{conductorId}/calificaciones")
    public List<CalificacionResponse> obtenerCalificacionesConductor(@PathVariable Long conductorId) {
        return viajeService.obtenerCalificacionesConductor(conductorId)
            .stream()
            .map(c -> new CalificacionResponse(
                c.getIdCalificacion(),
                c.getCalificador().getNombre(),
                c.getPuntuacion(),
                c.getComentario(),
                c.getCreadoEn()
            ))
            .toList();
    }

    /**
     * Obtiene el rating promedio de un conductor
     */
    @GetMapping("/conductor/{conductorId}/rating")
    public Map<String, Object> obtenerRatingConductor(@PathVariable Long conductorId) {
        Double rating = viajeService.calcularRatingPromedioConductor(conductorId);
        return Map.of(
            "conductorId", conductorId,
            "ratingPromedio", rating,
            "totalCalificaciones", viajeService.obtenerCalificacionesConductor(conductorId).size()
        );
    }

    // ==================== Utilidades ====================

    private ViajeResponse toResponse(Viaje viaje) {
        String pasajeroNombre = viaje.getPasajero() != null ? viaje.getPasajero().getNombre() : null;
        Long pasajeroId = viaje.getPasajero() != null ? viaje.getPasajero().getIdUsuario() : null;
        String conductorNombre = null;
        Long conductorId = null;
        if (viaje.getConductor() != null) {
            conductorId = viaje.getConductor().getIdConductor();
            if (viaje.getConductor().getUsuario() != null) {
                conductorNombre = viaje.getConductor().getUsuario().getNombre();
            }
        }

        return new ViajeResponse(
            viaje.getIdViaje(),
            pasajeroId,
            pasajeroNombre,
            conductorId,
            conductorNombre,
            viaje.getOrigenLat(),
            viaje.getOrigenLng(),
            viaje.getDestinoLat(),
            viaje.getDestinoLng(),
            viaje.getEstado() == null ? EstadoViaje.SOLICITADO : viaje.getEstado(),
            viaje.getPrecio(),
            viaje.getCreadoEn(),
            viaje.getActualizadoEn()
        );
    }

    private AuthResponse validarRol(String authorization, String tokenHeader, RolUsuario rol) {
        String token = AuthTokenExtractor.extraerToken(authorization, tokenHeader);
        AuthResponse session = authService.obtenerSesion(token);
        if (session.usuario() == null || session.usuario().rol() != rol) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
        }
        return session;
    }

    private AuthResponse validarRolMixto(String authorization, String tokenHeader) {
        String token = AuthTokenExtractor.extraerToken(authorization, tokenHeader);
        return authService.obtenerSesion(token);
    }
}

