package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.model.Notificacion;
import api.proyecto.redes.model.TipoNotificacion;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.NotificacionService;
import api.proyecto.redes.util.AuthTokenExtractor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    public NotificacionController(NotificacionService notificacionService,
                                 AuthService authService,
                                 UsuarioRepository usuarioRepository) {
        this.notificacionService = notificacionService;
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene las últimas 5 notificaciones del usuario autenticado
     */
    @GetMapping("/ultimas")
    public Map<String, Object> obtenerUltimas(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        List<Notificacion> notificaciones = notificacionService.obtenerUltimas(usuario.getIdUsuario());
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", notificaciones.size());
        response.put("notificaciones", notificaciones);
        return response;
    }

    /**
     * Obtiene notificaciones no leídas
     */
    @GetMapping("/no-leidas")
    public Map<String, Object> obtenerNoLeidas(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        List<Notificacion> noLeidas = notificacionService.obtenerNoLeidas(usuario.getIdUsuario());
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", noLeidas.size());
        response.put("notificaciones", noLeidas);
        return response;
    }

    /**
     * Cuenta notificaciones no leídas
     */
    @GetMapping("/contar-no-leidas")
    public Map<String, Object> contarNoLeidas(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        long cantidad = notificacionService.contarNoLeidas(usuario.getIdUsuario());
        
        Map<String, Object> response = new HashMap<>();
        response.put("noLeidas", cantidad);
        return response;
    }

    /**
     * Marca una notificación como leída
     */
    @PostMapping("/{id}/marcar-leida")
    public Map<String, Object> marcarComoLeida(@PathVariable Long id,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        validarYObtenerUsuario(authHeader);
        Notificacion notificacion = notificacionService.marcarComoLeida(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Notificación marcada como leída");
        response.put("notificacion", notificacion);
        return response;
    }

    /**
     * Marca todas las notificaciones del usuario como leídas
     */
    @PostMapping("/marcar-todas-leidas")
    public Map<String, Object> marcarTodasComoLeidas(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        notificacionService.marcarTodasComoLeidas(usuario.getIdUsuario());
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Todas las notificaciones marcadas como leídas");
        return response;
    }

    /**
     * Obtiene notificaciones por tipo
     */
    @GetMapping("/tipo/{tipo}")
    public Map<String, Object> obtenerPorTipo(@PathVariable String tipo,
                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        
        try {
            TipoNotificacion tipoEnum = TipoNotificacion.valueOf(tipo.toUpperCase());
            List<Notificacion> notificaciones = notificacionService.obtenerPorTipo(usuario.getIdUsuario(), tipoEnum);
            
            Map<String, Object> response = new HashMap<>();
            response.put("tipo", tipo);
            response.put("total", notificaciones.size());
            response.put("notificaciones", notificaciones);
            return response;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de notificación inválido");
        }
    }

    /**
     * Elimina una notificación
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> eliminarNotificacion(@PathVariable Long id,
                                                   @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        validarYObtenerUsuario(authHeader);
        notificacionService.eliminarNotificacion(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Notificación eliminada");
        return response;
    }

    // =================== MÉTODOS PRIVADOS ===================

    private Usuario validarYObtenerUsuario(String authHeader) {
        String token = AuthTokenExtractor.extraerToken(authHeader, null);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no proporcionado");
        }

        AuthResponse auth = authService.obtenerSesion(token);
        if (auth == null || auth.usuario() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        return usuarioRepository.findById(auth.usuario().idUsuario())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
