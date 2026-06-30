package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.ReporteResumenResponse;
import api.proyecto.redes.dto.ReporteSeriePunto;
import api.proyecto.redes.dto.UsuarioResponse;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.ReporteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteControllerWebTests {

    @Mock
    private ReporteService reporteService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ReporteController reporteController;

    @Test
    void reporteDia_debeResponder200ParaAdmin() throws Exception {
        when(authService.obtenerSesion(any())).thenReturn(adminSession());
        when(reporteService.reporteDia(any(LocalDate.class))).thenReturn(
            new ReporteResumenResponse("dia", "2026-06-30", 5L, 10L, 6L, 3L, 1L,
                List.of(new ReporteSeriePunto("2026-06-30", 5L)))
        );

        ReporteResumenResponse response = reporteController.reporteDia(null, "Bearer token-admin", null);
        assertEquals("dia", response.periodo());
        assertEquals(5L, response.viajes());
    }

    @Test
    void reporteDia_debeResponder403SiNoEsAdmin() throws Exception {
        AuthResponse noAdmin = new AuthResponse("token", new UsuarioResponse(2L, "User", "u@test", RolUsuario.PASAJERO, null));
        when(authService.obtenerSesion(any())).thenReturn(noAdmin);

        assertThrows(ResponseStatusException.class,
            () -> reporteController.reporteDia(null, "Bearer token-user", null));
    }

    @Test
    void reporteMes_debeResponder200() throws Exception {
        when(authService.obtenerSesion(any())).thenReturn(adminSession());
        when(reporteService.reporteMes(anyInt(), anyInt())).thenReturn(
            new ReporteResumenResponse("mes", "2026-06", 20L, 15L, 7L, 6L, 2L,
                List.of(new ReporteSeriePunto("2026-06-01", 2L)))
        );

        ReporteResumenResponse response = reporteController.reporteMes(2026, 6, "Bearer token-admin", null);
        assertEquals("2026-06", response.etiqueta());
    }

    private AuthResponse adminSession() {
        return new AuthResponse("token", new UsuarioResponse(1L, "Admin", "admin@test", RolUsuario.ADMIN, null));
    }
}
