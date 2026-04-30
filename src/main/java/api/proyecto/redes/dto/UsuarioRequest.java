package api.proyecto.redes.dto;

import api.proyecto.redes.model.RolUsuario;

public record UsuarioRequest(
    String nombre,
    String email,
    String password,
    RolUsuario rol
) {
}
