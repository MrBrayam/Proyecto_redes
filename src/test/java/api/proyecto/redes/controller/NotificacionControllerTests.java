package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.UsuarioResponse;
import api.proyecto.redes.model.Notificacion;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.TipoNotificacion;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTests {

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private NotificacionController notificacionController;

    @Test
    void obtenerUltimas_debeRetornarTotal() {
        when(authService.obtenerSesion(any())).thenReturn(authAdmin());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(adminUsuario()));
        when(notificacionService.obtenerUltimas(1L)).thenReturn(List.of(
            new Notificacion(adminUsuario(), "Titulo", "Mensaje", TipoNotificacion.INFORMACION)
        ));

        Map<String, Object> response = notificacionController.obtenerUltimas("Bearer token");

        assertEquals(1, response.get("total"));
    }

    @Test
    void contarNoLeidas_debeRetornarCantidad() {
        when(authService.obtenerSesion(any())).thenReturn(authAdmin());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(adminUsuario()));
        when(notificacionService.contarNoLeidas(1L)).thenReturn(3L);

        Map<String, Object> response = notificacionController.contarNoLeidas("Bearer token");

        assertEquals(3L, response.get("noLeidas"));
    }

    @Test
    void obtenerPorTipo_invalido_debeLanzar400() {
        when(authService.obtenerSesion(any())).thenReturn(authAdmin());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(adminUsuario()));

        assertThrows(ResponseStatusException.class,
            () -> notificacionController.obtenerPorTipo("no-existe", "Bearer token"));
    }

    @Test
    void eliminarNotificacion_debeResponderMensaje() {
        when(authService.obtenerSesion(any())).thenReturn(authAdmin());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(adminUsuario()));

        Map<String, Object> response = notificacionController.eliminarNotificacion(15L, "Bearer token");

        assertEquals("Notificación eliminada", response.get("mensaje"));
    }

    @Test
    void obtenerUltimas_sinToken_debeLanzar401() {
        assertThrows(ResponseStatusException.class,
            () -> notificacionController.obtenerUltimas("Bearer "));
    }

    private AuthResponse authAdmin() {
        return new AuthResponse("token", new UsuarioResponse(1L, "Admin", "admin@test", RolUsuario.ADMIN, null));
    }

    private Usuario adminUsuario() {
        Usuario u = new Usuario();
        u.setIdUsuario(1L);
        u.setRol(RolUsuario.ADMIN);
        return u;
    }
}
