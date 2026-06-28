package api.proyecto.redes.service;

import api.proyecto.redes.model.Notificacion;
import api.proyecto.redes.model.TipoNotificacion;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.repository.NotificacionRepository;
import api.proyecto.redes.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NotificacionService {
    
    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificacionService(NotificacionRepository notificacionRepository,
                              UsuarioRepository usuarioRepository,
                              SimpMessagingTemplate messagingTemplate) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Envía una notificación a un usuario y la guarda en BD
     */
    public Notificacion enviarNotificacion(Long usuarioId, String titulo, String mensaje, TipoNotificacion tipo) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Notificacion notificacion = new Notificacion(usuario, titulo, mensaje, tipo);
        notificacionRepository.save(notificacion);

        // Enviar por WebSocket en tiempo real
        enviarPorWebSocket(usuarioId, notificacion);

        return notificacion;
    }

    /**
     * Envía notificación relacionada a un viaje
     */
    public Notificacion enviarNotificacionViaje(Long usuarioId, String titulo, String mensaje, 
                                               TipoNotificacion tipo, Long viajeId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Notificacion notificacion = new Notificacion(usuario, titulo, mensaje, tipo, viajeId);
        notificacionRepository.save(notificacion);

        // Enviar por WebSocket
        enviarPorWebSocket(usuarioId, notificacion);

        return notificacion;
    }

    /**
     * Envía una notificación por WebSocket a tema privado del usuario
     */
    private void enviarPorWebSocket(Long usuarioId, Notificacion notificacion) {
        try {
            messagingTemplate.convertAndSend(
                "/topic/notificaciones/usuario/" + usuarioId,
                notificacion
            );
        } catch (Exception e) {
            // Si falla WebSocket, la notificación ya está guardada en BD
            System.err.println("Error enviando notificación por WebSocket: " + e.getMessage());
        }
    }

    /**
     * Obtiene últimas 5 notificaciones del usuario
     */
    public List<Notificacion> obtenerUltimas(Long usuarioId) {
        return notificacionRepository.obtenerUltimas(usuarioId, 5);
    }

    /**
     * Obtiene todas las notificaciones no leídas
     */
    public List<Notificacion> obtenerNoLeidas(Long usuarioId) {
        return notificacionRepository.obtenerNoLeidas(usuarioId);
    }

    /**
     * Cuenta notificaciones sin leer
     */
    public long contarNoLeidas(Long usuarioId) {
        return notificacionRepository.contarNoLeidas(usuarioId);
    }

    /**
     * Marca una notificación como leída
     */
    public Notificacion marcarComoLeida(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificación no encontrada"));

        notificacion.marcarComoLeida();
        return notificacionRepository.save(notificacion);
    }

    /**
     * Marca todas las notificaciones del usuario como leídas
     */
    public void marcarTodasComoLeidas(Long usuarioId) {
        List<Notificacion> noLeidas = obtenerNoLeidas(usuarioId);
        noLeidas.forEach(Notificacion::marcarComoLeida);
        notificacionRepository.saveAll(noLeidas);
    }

    /**
     * Obtiene notificaciones por tipo
     */
    public List<Notificacion> obtenerPorTipo(Long usuarioId, TipoNotificacion tipo) {
        return notificacionRepository.obtenerPorTipo(usuarioId, tipo);
    }

    /**
     * Elimina una notificación
     */
    public void eliminarNotificacion(Long notificacionId) {
        notificacionRepository.deleteById(notificacionId);
    }

    /**
     * Limpia notificaciones antiguas (más de 30 días)
     */
    public void limpiarNotificacionesAntiguas() {
        // Implementar si es necesario (task programada)
    }
}
