package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.MetodoPagoRequest;
import api.proyecto.redes.dto.MetodoPagoResponse;
import api.proyecto.redes.dto.TransaccionResponse;
import api.proyecto.redes.dto.UsuarioResponse;
import api.proyecto.redes.model.EstadoTransaccion;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.TipoMetodoPago;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.PagoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoControllerTests {

    @Mock
    private PagoService pagoService;

    @Mock
    private AuthService authService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PagoController pagoController;

    @Test
    void obtenerMetodosPago_debeRetornarListado() {
        when(authService.obtenerSesion(any())).thenReturn(adminAuth());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(adminUsuario()));
        when(pagoService.obtenerMetodosPago(1L)).thenReturn(List.of(
            new MetodoPagoResponse(10L, TipoMetodoPago.TARJETA_CREDITO, "1234", "Admin", true, LocalDateTime.now())
        ));

        Map<String, Object> response = pagoController.obtenerMetodosPago("Bearer token");

        assertEquals(1, response.get("total"));
    }

    @Test
    void crearIntentoTransaccion_debeRetornarDatosClave() {
        when(authService.obtenerSesion(any())).thenReturn(adminAuth());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(adminUsuario()));
        when(pagoService.crearTransaccionPendiente(anyLong(), anyLong(), anyLong())).thenReturn(
            new TransaccionResponse(
                22L,
                33L,
                new BigDecimal("50.00"),
                new BigDecimal("5.00"),
                new BigDecimal("45.00"),
                EstadoTransaccion.PENDIENTE,
                "TARJETA_CREDITO",
                "Pago por viaje",
                null,
                LocalDateTime.now(),
                null
            )
        );

        Map<String, Object> response = pagoController.crearIntentoTransaccion(33L, 10L, "Bearer token");

        assertEquals(22L, response.get("transaccionId"));
        assertEquals("pi_test_22", response.get("clientSecret"));
    }

    @Test
    void obtenerComisionesTotal_debeRechazarNoAdmin() {
        AuthResponse pasajeroAuth = new AuthResponse(
            "token",
            new UsuarioResponse(2L, "Pas", "pas@test", RolUsuario.PASAJERO, null)
        );
        Usuario pasajero = new Usuario();
        pasajero.setIdUsuario(2L);
        pasajero.setRol(RolUsuario.PASAJERO);

        when(authService.obtenerSesion(any())).thenReturn(pasajeroAuth);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(pasajero));

        assertThrows(ResponseStatusException.class,
            () -> pagoController.obtenerComisionesTotal("Bearer token"));
    }

    @Test
    void agregarMetodoPago_debeResponderConMensaje() {
        when(authService.obtenerSesion(any())).thenReturn(adminAuth());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(adminUsuario()));

        MetodoPagoRequest req = new MetodoPagoRequest(TipoMetodoPago.TARJETA_DEBITO, "tok_1234", "Admin", true);
        when(pagoService.agregarMetodoPago(1L, req)).thenReturn(
            new MetodoPagoResponse(11L, TipoMetodoPago.TARJETA_DEBITO, "1234", "Admin", true, LocalDateTime.now())
        );

        Map<String, Object> response = pagoController.agregarMetodoPago(req, "Bearer token");
        assertEquals("Método de pago agregado exitosamente", response.get("mensaje"));
    }

    private AuthResponse adminAuth() {
        return new AuthResponse("token", new UsuarioResponse(1L, "Admin", "admin@test", RolUsuario.ADMIN, null));
    }

    private Usuario adminUsuario() {
        Usuario u = new Usuario();
        u.setIdUsuario(1L);
        u.setRol(RolUsuario.ADMIN);
        return u;
    }
}
