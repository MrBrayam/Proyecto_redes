package api.proyecto.redes.service;

import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class PasajeroService {

    private final UsuarioRepository usuarioRepository;

    public PasajeroService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findByRol(RolUsuario.PASAJERO);
    }

    public Usuario obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pasajero no encontrado"));
        if (usuario.getRol() != RolUsuario.PASAJERO) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pasajero no encontrado");
        }
        return usuario;
    }

    public Usuario crear(Usuario usuario) {
        validarBasico(usuario);
        usuario.setRol(RolUsuario.PASAJERO);
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Usuario existente = obtenerPorId(id);

        if (datos.getNombre() != null && !datos.getNombre().isBlank()) {
            existente.setNombre(datos.getNombre());
        }
        if (datos.getEmail() != null && !datos.getEmail().isBlank()) {
            if (!datos.getEmail().equalsIgnoreCase(existente.getEmail())
                && usuarioRepository.findByEmail(datos.getEmail()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
            }
            existente.setEmail(datos.getEmail());
        }
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            existente.setPassword(datos.getPassword());
        }

        return usuarioRepository.save(existente);
    }

    public void eliminar(Long id) {
        Usuario existente = obtenerPorId(id);
        usuarioRepository.delete(existente);
    }

    private void validarBasico(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "nombre es requerido");
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email es requerido");
        }
        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password es requerido");
        }
    }
}
