package api.proyecto.redes.dto;

public record AuthResponse(
    String token,
    UsuarioResponse usuario
) {
}
