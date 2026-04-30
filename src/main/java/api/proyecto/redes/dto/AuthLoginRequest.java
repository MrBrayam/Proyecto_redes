package api.proyecto.redes.dto;

public record AuthLoginRequest(
    String email,
    String password
) {
}
