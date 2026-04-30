package api.proyecto.redes.dto;

public record AuthRegisterRequest(
    String nombre,
    String email,
    String password
) {
}
