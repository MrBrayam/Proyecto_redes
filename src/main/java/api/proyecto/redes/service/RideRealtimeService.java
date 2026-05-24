package api.proyecto.redes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RideRealtimeService {

    private final ObjectMapper objectMapper;
    private final Map<Long, WebSocketSession> pasajeroSessions = new ConcurrentHashMap<>();
    private final Map<Long, WebSocketSession> conductorSessions = new ConcurrentHashMap<>();

    public RideRealtimeService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void registerPasajero(Long usuarioId, WebSocketSession session) {
        if (usuarioId != null) {
            pasajeroSessions.put(usuarioId, session);
        }
    }

    public void registerConductor(Long conductorId, WebSocketSession session) {
        if (conductorId != null) {
            conductorSessions.put(conductorId, session);
        }
    }

    public void removeSession(WebSocketSession session) {
        if (session == null) {
            return;
        }
        pasajeroSessions.entrySet().removeIf(entry -> session.equals(entry.getValue()));
        conductorSessions.entrySet().removeIf(entry -> session.equals(entry.getValue()));
    }

    public void enviarAPasajero(Long usuarioId, Object payload) {
        WebSocketSession session = pasajeroSessions.get(usuarioId);
        sendMessage(session, payload);
    }

    public void enviarAConductor(Long conductorId, Object payload) {
        WebSocketSession session = conductorSessions.get(conductorId);
        sendMessage(session, payload);
    }

    public void broadcastConductores(Object payload) {
        for (WebSocketSession session : conductorSessions.values()) {
            sendMessage(session, payload);
        }
    }

    private void sendMessage(WebSocketSession session, Object payload) {
        if (session == null || !session.isOpen()) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(payload);
            session.sendMessage(new TextMessage(json));
        } catch (IOException ignored) {
            // Ignore send failures; client will reconnect
        }
    }
}
