package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.ConductorProfileResponse;
import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.ConductorService;
import api.proyecto.redes.util.AuthTokenExtractor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/conductor")
public class ConductorProfileController {

    private final AuthService authService;
    private final ConductorService conductorService;

    public ConductorProfileController(AuthService authService, ConductorService conductorService) {
        this.authService = authService;
        this.conductorService = conductorService;
    }

    @GetMapping("/me")
    public ConductorProfileResponse obtenerPerfil(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {

        AuthResponse session = validarRol(authorization, tokenHeader, RolUsuario.CONDUCTOR);
        Conductor conductor = conductorService.obtenerPorUsuarioId(session.usuario().idUsuario());
        return new ConductorProfileResponse(
            conductor.getIdConductor(),
            session.usuario().idUsuario(),
            session.usuario().nombre(),
            session.usuario().email(),
            conductor.getLicencia(),
            conductor.getVehiculo(),
            conductor.getCalificacionPromedio(),
            conductor.getDisponible()
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
}
