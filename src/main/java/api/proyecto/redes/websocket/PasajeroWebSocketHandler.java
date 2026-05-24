package api.proyecto.redes.websocket;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.ViajeRequest;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.model.Viaje;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.RideRealtimeService;
import api.proyecto.redes.service.ViajeService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.math.BigDecimal;
import java.util.Map;

public class PasajeroWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final ViajeService viajeService;
    private final RideRealtimeService rideRealtimeService;

    public PasajeroWebSocketHandler(ObjectMapper objectMapper,
                                    AuthService authService,
                                    UsuarioRepository usuarioRepository,
                                    ViajeService viajeService,
                                    RideRealtimeService rideRealtimeService) {
        this.objectMapper = objectMapper;
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
        this.viajeService = viajeService;
        this.rideRealtimeService = rideRealtimeService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode payload = objectMapper.readTree(message.getPayload());
        String type = payload.path("type").asText("");
        if ("ride-request".equals(type)) {
            handleRideRequest(session, payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        rideRealtimeService.removeSession(session);
    }

    private void handleRideRequest(WebSocketSession session, JsonNode payload) throws Exception {
        String token = payload.path("token").asText(null);
        AuthResponse auth = authService.obtenerSesion(token);
        if (auth.usuario() == null || auth.usuario().rol() != RolUsuario.PASAJERO) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                Map.of("type", "error", "message", "Rol no autorizado")
            )));
            return;
        }

        Usuario pasajero = usuarioRepository.findById(auth.usuario().idUsuario()).orElse(null);
        if (pasajero == null) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                Map.of("type", "error", "message", "Pasajero no encontrado")
            )));
            return;
        }

        BigDecimal origenLat = toDecimal(payload.path("origenLat").asText(null));
        BigDecimal origenLng = toDecimal(payload.path("origenLng").asText(null));
        BigDecimal destinoLat = toDecimal(payload.path("destinoLat").asText(null));
        BigDecimal destinoLng = toDecimal(payload.path("destinoLng").asText(null));
        String destinoTexto = payload.path("destino").asText(null);

        if (origenLat == null || origenLng == null) {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
                Map.of("type", "error", "message", "Origen invalido")
            )));
            return;
        }
        if (destinoLat == null || destinoLng == null) {
            destinoLat = origenLat;
            destinoLng = origenLng;
        }

        ViajeRequest request = new ViajeRequest(origenLat, origenLng, destinoLat, destinoLng, destinoTexto);
        Viaje viaje = viajeService.crearSolicitud(pasajero, request);

        rideRealtimeService.registerPasajero(pasajero.getIdUsuario(), session);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(
            Map.of("type", "ride-status", "status", "SOLICITADO", "viajeId", viaje.getIdViaje())
        )));

        rideRealtimeService.broadcastConductores(Map.of(
            "type", "ride-request",
            "viajeId", viaje.getIdViaje(),
            "pasajeroId", pasajero.getIdUsuario(),
            "pasajeroNombre", pasajero.getNombre(),
            "origenLat", viaje.getOrigenLat(),
            "origenLng", viaje.getOrigenLng(),
            "destinoLat", viaje.getDestinoLat(),
            "destinoLng", viaje.getDestinoLng(),
            "destino", destinoTexto
        ));
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
