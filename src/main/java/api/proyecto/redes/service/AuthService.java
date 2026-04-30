package api.proyecto.redes.service;

import api.proyecto.redes.dto.AuthLoginRequest;
import api.proyecto.redes.dto.AuthRegisterRequest;
import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.UsuarioResponse;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.util.PasswordHasher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private static final Duration TOKEN_TTL = Duration.ofHours(8);

    private final UsuarioRepository usuarioRepository;
    private final Map<String, AuthSession> sesiones = new ConcurrentHashMap<>();

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse registrar(AuthRegisterRequest request) {
        validarRegistro(request);

        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(PasswordHasher.hash(request.password()));
        usuario.setRol(RolUsuario.PASAJERO);

        Usuario creado = usuarioRepository.save(usuario);
        return toUsuarioResponse(creado);
    }

    public AuthResponse login(AuthLoginRequest request) {
        validarLogin(request);

        Usuario usuario = usuarioRepository.findByEmail(request.email())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));

        if (!PasswordHasher.matches(request.password(), usuario.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
        }

        AuthSession session = crearSesion(usuario);
        return new AuthResponse(session.getToken(), toUsuarioResponse(usuario));
    }

    public AuthResponse obtenerSesion(String token) {
        AuthSession session = validarToken(token);
        UsuarioResponse usuario = new UsuarioResponse(
            session.getUsuarioId(),
            session.getNombre(),
            session.getEmail(),
            session.getRol(),
            null
        );
        return new AuthResponse(session.getToken(), usuario);
    }

    public void logout(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }

    private AuthSession crearSesion(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        Instant expiraEn = Instant.now().plus(TOKEN_TTL);
        AuthSession session = new AuthSession(
            token,
            usuario.getIdUsuario(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol(),
            expiraEn
        );
        sesiones.put(token, session);
        return session;
    }

    private AuthSession validarToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }

        AuthSession session = sesiones.get(token);
        if (session == null || session.getExpiraEn().isBefore(Instant.now())) {
            sesiones.remove(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido o expirado");
        }
        return session;
    }

    private void validarRegistro(AuthRegisterRequest request) {
        if (request == null
            || request.nombre() == null || request.nombre().isBlank()
            || request.email() == null || request.email().isBlank()
            || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre, email y password son requeridos");
        }
    }

    private void validarLogin(AuthLoginRequest request) {
        if (request == null
            || request.email() == null || request.email().isBlank()
            || request.password() == null || request.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email y password son requeridos");
        }
    }

    private UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
            usuario.getIdUsuario(),
            usuario.getNombre(),
            usuario.getEmail(),
            usuario.getRol(),
            usuario.getCreadoEn()
        );
    }
}
