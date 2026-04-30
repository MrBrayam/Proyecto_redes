package api.proyecto.redes.controller;

import api.proyecto.redes.dto.AuthLoginRequest;
import api.proyecto.redes.dto.AuthRegisterRequest;
import api.proyecto.redes.dto.AuthResponse;
import api.proyecto.redes.dto.UsuarioResponse;
import api.proyecto.redes.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@RequestBody AuthRegisterRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                           @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        String token = extraerToken(authorization, tokenHeader);
        return ResponseEntity.ok(authService.obtenerSesion(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                       @RequestHeader(value = "X-Auth-Token", required = false) String tokenHeader) {
        String token = extraerToken(authorization, tokenHeader);
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    private String extraerToken(String authorization, String tokenHeader) {
        if (tokenHeader != null && !tokenHeader.isBlank()) {
            return tokenHeader.trim();
        }
        if (authorization == null || authorization.isBlank()) {
            return null;
        }
        if (authorization.toLowerCase().startsWith("bearer ")) {
            return authorization.substring(7).trim();
        }
        return authorization.trim();
    }
}
