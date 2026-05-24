package api.proyecto.redes.websocket;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.model.EstadoViaje;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.Viaje;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.ConductorService;
import api.proyecto.redes.service.RideRealtimeService;
import api.proyecto.redes.service.ViajeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ConductorWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final ConductorService conductorService;
    private final ViajeService viajeService;
    private final RideRealtimeService rideRealtimeService;

    public ConductorWebSocketHandler(ObjectMapper objectMapper,
                                     AuthService authService,
                                     ConductorService conductorService,
                                     ViajeService viajeService,
                                     RideRealtimeService rideRealtimeService) {
        this.objectMapper = objectMapper;
        this.authService = authService;
        this.conductorService = conductorService;
        this.viajeService = viajeService;
        this.rideRealtimeService = rideRealtimeService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode payload = objectMapper.readTree(message.getPayload());
        String type = payload.path("type").asText("");
        switch (type) {
            case "driver-connect" -> handleDriverConnect(session, payload);
            case "ride-accept" -> handleRideAccept(session, payload);
            case "ride-reject" -> handleRideReject(session, payload);
            case "driver-location" -> handleDriverLocation(session, payload);
            case "driver-available-location" -> handleAvailableLocation(session, payload);
            default -> {
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        rideRealtimeService.removeSession(session);
    }

    private void handleDriverConnect(WebSocketSession session, JsonNode payload) throws Exception {
        AuthResponse auth = validarConductor(payload, session);
        if (auth == null) {
            return;
        }
        Conductor conductor = conductorService.obtenerPorUsuarioId(auth.usuario().idUsuario());
        rideRealtimeService.registerConductor(conductor.getIdConductor(), session);

        List<Viaje> pendientes = viajeService.listarPendientes();
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
            Map.of("type", "driver-ready", "pendientes", pendientes.stream().map(this::toPayload).toList())
        )));
    }

    private void handleRideAccept(WebSocketSession session, JsonNode payload) throws Exception {
        AuthResponse auth = validarConductor(payload, session);
        if (auth == null) {
            return;
        }
        Long viajeId = payload.path("viajeId").asLong(0);
        Conductor conductor = conductorService.obtenerPorUsuarioId(auth.usuario().idUsuario());
        Viaje viaje = viajeService.aceptarViaje(viajeId, conductor);

        rideRealtimeService.enviarAPasajero(viaje.getPasajero().getIdUsuario(), Map.of(
            "type", "ride-status",
            "status", EstadoViaje.ACEPTADO.name(),
            "viajeId", viaje.getIdViaje(),
            "conductorNombre", conductor.getUsuario().getNombre()
        ));

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
            Map.of("type", "ride-accepted", "viajeId", viaje.getIdViaje())
        )));
    }

    private void handleRideReject(WebSocketSession session, JsonNode payload) throws Exception {
        AuthResponse auth = validarConductor(payload, session);
        if (auth == null) {
            return;
        }
        Long viajeId = payload.path("viajeId").asLong(0);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
            Map.of("type", "ride-rejected", "viajeId", viajeId)
        )));
    }

    private void handleDriverLocation(WebSocketSession session, JsonNode payload) throws Exception {
        AuthResponse auth = validarConductor(payload, session);
        if (auth == null) {
            return;
        }
        Long viajeId = payload.path("viajeId").asLong(0);
        BigDecimal lat = toDecimal(payload.path("lat").asText(null));
        BigDecimal lng = toDecimal(payload.path("lng").asText(null));
        if (lat == null || lng == null) {
            return;
        }
        Viaje viaje = viajeService.obtenerPorId(viajeId);
        if (viaje.getConductor() == null || !viaje.getConductor().getUsuario().getIdUsuario().equals(auth.usuario().idUsuario())) {
            return;
        }
        rideRealtimeService.enviarAPasajero(viaje.getPasajero().getIdUsuario(), Map.of(
            "type", "driver-location",
            "lat", lat,
            "lng", lng,
            "viajeId", viajeId
        ));

        rideRealtimeService.updateConductorLocation(viaje.getConductor().getIdConductor(), lat.doubleValue(), lng.doubleValue());
    }

    private void handleAvailableLocation(WebSocketSession session, JsonNode payload) throws Exception {
        AuthResponse auth = validarConductor(payload, session);
        if (auth == null) {
            return;
        }
        BigDecimal lat = toDecimal(payload.path("lat").asText(null));
        BigDecimal lng = toDecimal(payload.path("lng").asText(null));
        if (lat == null || lng == null) {
            return;
        }
        Conductor conductor = conductorService.obtenerPorUsuarioId(auth.usuario().idUsuario());
        rideRealtimeService.updateConductorLocation(conductor.getIdConductor(), lat.doubleValue(), lng.doubleValue());
    }

    private AuthResponse validarConductor(JsonNode payload, WebSocketSession session) throws Exception {
        String token = payload.path("token").asText(null);
        AuthResponse auth = authService.obtenerSesion(token);
        if (auth.usuario() == null || auth.usuario().rol() != RolUsuario.CONDUCTOR) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                Map.of("type", "error", "message", "Rol no autorizado")
            )));
            return null;
        }
        return auth;
    }

    private Map<String, Object> toPayload(Viaje viaje) {
        return Map.of(
            "viajeId", viaje.getIdViaje(),
            "pasajeroId", viaje.getPasajero().getIdUsuario(),
            "pasajeroNombre", viaje.getPasajero().getNombre(),
            "origenLat", viaje.getOrigenLat(),
            "origenLng", viaje.getOrigenLng(),
            "destinoLat", viaje.getDestinoLat(),
            "destinoLng", viaje.getDestinoLng(),
            "estado", viaje.getEstado().name()
        );
    }

    private BigDecimal toDecimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
