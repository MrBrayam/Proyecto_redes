package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.ConductorCercanoResponse;
import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.ConductorLocationSnapshot;
import api.proyecto.redes.service.ConductorService;
import api.proyecto.redes.service.RideRealtimeService;
import api.proyecto.redes.util.AuthTokenExtractor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pasajero/conductores")
public class ConductorCercanoController {

    private final AuthService authService;
    private final ConductorService conductorService;
    private final RideRealtimeService rideRealtimeService;

    public ConductorCercanoController(AuthService authService,
                                      ConductorService conductorService,
                                      RideRealtimeService rideRealtimeService) {
        this.authService = authService;
        this.conductorService = conductorService;
        this.rideRealtimeService = rideRealtimeService;
    }

    @GetMapping("/cercanos")
    public List<ConductorCercanoResponse> listarCercanos(
        @RequestParam("lat") double lat,
        @RequestParam("lng") double lng,
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        validarPasajero(authorization, tokenHeader);
        Map<Long, ConductorLocationSnapshot> locations = rideRealtimeService.getConductorLocations();
        return conductorService.listarDisponibles().stream()
            .map(conductor -> toCercano(conductor, locations.get(conductor.getIdConductor()), lat, lng))
            .flatMap(Optional::stream)
            .sorted(Comparator.comparingDouble(ConductorCercanoResponse::distanciaKm))
            .limit(8)
            .toList();
    }

    private Optional<ConductorCercanoResponse> toCercano(Conductor conductor,
                                                        ConductorLocationSnapshot snapshot,
                                                        double origenLat,
                                                        double origenLng) {
        double distancia = snapshot == null
            ? -1
            : haversineKm(origenLat, origenLng, snapshot.lat(), snapshot.lng());
        String nombre = conductor.getUsuario() != null ? conductor.getUsuario().getNombre() : "Conductor";
        return Optional.of(new ConductorCercanoResponse(
            conductor.getIdConductor(),
            nombre,
            conductor.getVehiculo(),
            conductor.getCalificacionPromedio(),
            Boolean.TRUE.equals(conductor.getDisponible()),
            distancia
        ));
    }

    private void validarPasajero(String authorization, String tokenHeader) {
        String token = AuthTokenExtractor.extraerToken(authorization, tokenHeader);
        AuthResponse session = authService.obtenerSesion(token);
        if (session.usuario() == null || session.usuario().rol() != RolUsuario.PASAJERO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
        }
    }

    private double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        double r = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }
}
