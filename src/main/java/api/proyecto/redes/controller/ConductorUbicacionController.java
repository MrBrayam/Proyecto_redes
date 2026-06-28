package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.ConductorCercanoResponse;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.ConductorService;
import api.proyecto.redes.service.ConductorUbicacionService;
import api.proyecto.redes.util.AuthTokenExtractor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/conductores")
public class ConductorUbicacionController {

    private final ConductorUbicacionService ubicacionService;
    private final ConductorService conductorService;
    private final AuthService authService;

    public ConductorUbicacionController(ConductorUbicacionService ubicacionService,
                                       ConductorService conductorService,
                                       AuthService authService) {
        this.ubicacionService = ubicacionService;
        this.conductorService = conductorService;
        this.authService = authService;
    }

    /**
     * FASE 1: Actualiza la ubicación del conductor (en tiempo real)
     */
    @PostMapping("/{id}/ubicacion")
    public Map<String, Object> actualizarUbicacion(@PathVariable Long id,
                                                   @RequestParam BigDecimal latitud,
                                                   @RequestParam BigDecimal longitud,
                                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                                   @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRolConductor(authorization, tokenHeader);
        
        // Verificar que es el conductor autenticado
        if (!esElConductorAutenticado(id, session)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para actualizar esta ubicación");
        }

        var ubicacion = ubicacionService.actualizarUbicacion(id, latitud, longitud);

        return Map.of(
            "idUbicacion", ubicacion.getIdUbicacion(),
            "conductorId", ubicacion.getConductor().getIdConductor(),
            "latitud", ubicacion.getLatitud(),
            "longitud", ubicacion.getLongitud(),
            "actualizadoEn", ubicacion.getActualizadoEn(),
            "mensaje", "Ubicación actualizada"
        );
    }

    /**
     * FASE 1: Busca conductores cercanos a una ubicación
     */
    @GetMapping("/cercanos")
    public List<ConductorCercanoResponse> buscarConductoresCercanos(
        @RequestParam BigDecimal latitud,
        @RequestParam BigDecimal longitud,
        @RequestParam(value = "radio", defaultValue = "5") double radioKm) {
        
        return ubicacionService.buscarConductoresCercanos(latitud, longitud, radioKm);
    }

    /**
     * Obtiene la ubicación actual de un conductor (solo para uso interno/admin)
     */
    @GetMapping("/{id}/ubicacion")
    public Map<String, Object> obtenerUbicacion(@PathVariable Long id) {
        var ubicacion = ubicacionService.obtenerUbicacion(id);

        return Map.of(
            "conductorId", ubicacion.getConductor().getIdConductor(),
            "conductorNombre", ubicacion.getConductor().getUsuario().getNombre(),
            "latitud", ubicacion.getLatitud(),
            "longitud", ubicacion.getLongitud(),
            "actualizadoEn", ubicacion.getActualizadoEn()
        );
    }

    private AuthResponse validarRolConductor(String authorization, String tokenHeader) {
        String token = AuthTokenExtractor.extraerToken(authorization, tokenHeader);
        AuthResponse session = authService.obtenerSesion(token);
        if (session.usuario() == null || session.usuario().rol() != RolUsuario.CONDUCTOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
        }
        return session;
    }

    private boolean esElConductorAutenticado(Long conductorId, AuthResponse session) {
        try {
            var conductor = conductorService.obtenerPorId(conductorId);
            return conductor.getUsuario().getIdUsuario().equals(session.usuario().idUsuario());
        } catch (Exception e) {
            return false;
        }
    }
}
