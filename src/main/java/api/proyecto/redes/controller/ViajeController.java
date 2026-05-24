package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.ViajeRequest;
import api.proyecto.redes.dto.ViajeResponse;
import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.model.EstadoViaje;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.model.Viaje;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.ConductorService;
import api.proyecto.redes.service.ViajeService;
import api.proyecto.redes.util.AuthTokenExtractor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/viajes")
public class ViajeController {

    private final ViajeService viajeService;
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final ConductorService conductorService;

    public ViajeController(ViajeService viajeService,
                           AuthService authService,
                           UsuarioRepository usuarioRepository,
                           ConductorService conductorService) {
        this.viajeService = viajeService;
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
        this.conductorService = conductorService;
    }

    @PostMapping
    public ViajeResponse crear(@RequestBody ViajeRequest request,
                              @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                              @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRol(authorization, tokenHeader, RolUsuario.PASAJERO);
        Usuario pasajero = usuarioRepository.findById(session.usuario().idUsuario())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pasajero no encontrado"));
        Viaje viaje = viajeService.crearSolicitud(pasajero, request);
        return toResponse(viaje);
    }

    @GetMapping("/pasajero")
    public List<ViajeResponse> listarPorPasajero(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRol(authorization, tokenHeader, RolUsuario.PASAJERO);
        return viajeService.listarPorPasajero(session.usuario().idUsuario()).stream().map(this::toResponse).toList();
    }

    @GetMapping("/pendientes")
    public List<ViajeResponse> listarPendientes(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
        @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        validarRol(authorization, tokenHeader, RolUsuario.CONDUCTOR);
        return viajeService.listarPendientes().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ViajeResponse obtener(@PathVariable Long id,
                                 @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                 @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRolMixto(authorization, tokenHeader);
        Viaje viaje = viajeService.obtenerPorId(id);
        if (session.usuario().rol() == RolUsuario.PASAJERO
            && !session.usuario().idUsuario().equals(viaje.getPasajero().getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin acceso al viaje");
        }
        return toResponse(viaje);
    }

    @PostMapping("/{id}/aceptar")
    public ViajeResponse aceptar(@PathVariable Long id,
                                 @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                 @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRol(authorization, tokenHeader, RolUsuario.CONDUCTOR);
        Conductor conductor = conductorService.obtenerPorUsuarioId(session.usuario().idUsuario());
        Viaje viaje = viajeService.aceptarViaje(id, conductor);
        return toResponse(viaje);
    }

    @PostMapping("/{id}/rechazar")
    public ViajeResponse rechazar(@PathVariable Long id,
                                  @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                  @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        validarRol(authorization, tokenHeader, RolUsuario.CONDUCTOR);
        Viaje viaje = viajeService.rechazarViaje(id);
        return toResponse(viaje);
    }

    @PostMapping("/{id}/finalizar")
    public ViajeResponse finalizar(@PathVariable Long id,
                                   @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                   @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        AuthResponse session = validarRolMixto(authorization, tokenHeader);
        if (session.usuario().rol() != RolUsuario.CONDUCTOR && session.usuario().rol() != RolUsuario.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
        }
        Viaje viaje = viajeService.finalizarViaje(id);
        return toResponse(viaje);
    }

    private ViajeResponse toResponse(Viaje viaje) {
        String pasajeroNombre = viaje.getPasajero() != null ? viaje.getPasajero().getNombre() : null;
        Long pasajeroId = viaje.getPasajero() != null ? viaje.getPasajero().getIdUsuario() : null;
        String conductorNombre = null;
        Long conductorId = null;
        if (viaje.getConductor() != null) {
            conductorId = viaje.getConductor().getIdConductor();
            if (viaje.getConductor().getUsuario() != null) {
                conductorNombre = viaje.getConductor().getUsuario().getNombre();
            }
        }

        return new ViajeResponse(
            viaje.getIdViaje(),
            pasajeroId,
            pasajeroNombre,
            conductorId,
            conductorNombre,
            viaje.getOrigenLat(),
            viaje.getOrigenLng(),
            viaje.getDestinoLat(),
            viaje.getDestinoLng(),
            viaje.getEstado() == null ? EstadoViaje.SOLICITADO : viaje.getEstado(),
            viaje.getPrecio(),
            viaje.getCreadoEn(),
            viaje.getActualizadoEn()
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

    private AuthResponse validarRolMixto(String authorization, String tokenHeader) {
        String token = AuthTokenExtractor.extraerToken(authorization, tokenHeader);
        return authService.obtenerSesion(token);
    }
}
