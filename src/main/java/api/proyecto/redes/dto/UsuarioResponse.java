package api.proyecto.redes.dto;

import api.proyecto.redes.model.RolUsuario;
import java.time.LocalDateTime;

public record UsuarioResponse(
    Long idUsuario,
    String nombre,
    String email,
    RolUsuario rol,
    LocalDateTime creadoEn
) {
}
