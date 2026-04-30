package api.proyecto.redes.service;

import api.proyecto.redes.model.RolUsuario;
import java.time.Instant;

public class AuthSession {

    private final String token;
    private final Long usuarioId;
    private final String nombre;
    private final String email;
    private final RolUsuario rol;
    private final Instant expiraEn;

    public AuthSession(String token, Long usuarioId, String nombre, String email, RolUsuario rol, Instant expiraEn) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.expiraEn = expiraEn;
    }

    public String getToken() {
        return token;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public Instant getExpiraEn() {
        return expiraEn;
    }
}
