package api.proyecto.redes.config;

import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.ConductorService;
import api.proyecto.redes.service.RideRealtimeService;
import api.proyecto.redes.service.ViajeService;
import api.proyecto.redes.websocket.ConductorWebSocketHandler;
import api.proyecto.redes.websocket.PasajeroWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ObjectMapper objectMapper;
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final ConductorService conductorService;
    private final ViajeService viajeService;
    private final RideRealtimeService rideRealtimeService;

    public WebSocketConfig(ObjectMapper objectMapper,
                           AuthService authService,
                           UsuarioRepository usuarioRepository,
                           ConductorService conductorService,
                           ViajeService viajeService,
                           RideRealtimeService rideRealtimeService) {
        this.objectMapper = objectMapper;
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
        this.conductorService = conductorService;
        this.viajeService = viajeService;
        this.rideRealtimeService = rideRealtimeService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new PasajeroWebSocketHandler(
            objectMapper, authService, usuarioRepository, viajeService, rideRealtimeService
        ), "/ws/pasajero").setAllowedOrigins("*");

        registry.addHandler(new ConductorWebSocketHandler(
            objectMapper, authService, conductorService, viajeService, rideRealtimeService
        ), "/ws/conductor").setAllowedOrigins("*");
    }
}
