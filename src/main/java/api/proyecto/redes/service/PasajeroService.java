package api.proyecto.redes.service;

import api.proyecto.redes.model.Pasajero;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.repository.PasajeroRepository;
import api.proyecto.redes.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class PasajeroService {

    private final UsuarioRepository usuarioRepository;
    private final PasajeroRepository pasajeroRepository;

    public PasajeroService(UsuarioRepository usuarioRepository, PasajeroRepository pasajeroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.pasajeroRepository = pasajeroRepository;
    }

    public List<Usuario> listar() {
        return pasajeroRepository.findAll().stream()
            .map(Pasajero::getUsuario)
            .toList();
    }

    public Usuario obtenerPorId(Long id) {
        Pasajero pasajero = pasajeroRepository.findByUsuario_IdUsuario(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pasajero no encontrado"));
        return pasajero.getUsuario();
    }

    public Usuario crear(Usuario usuario) {
        validarBasico(usuario);
        usuario.setRol(RolUsuario.PASAJERO);
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
        }
        Usuario creado = usuarioRepository.save(usuario);

        Pasajero pasajero = new Pasajero();
        pasajero.setUsuario(creado);
        pasajeroRepository.save(pasajero);

        return creado;
    }

    public Usuario actualizar(Long id, Usuario datos) {
        Pasajero pasajero = pasajeroRepository.findByUsuario_IdUsuario(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pasajero no encontrado"));
        Usuario existente = pasajero.getUsuario();

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
        Pasajero pasajero = pasajeroRepository.findByUsuario_IdUsuario(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pasajero no encontrado"));
        pasajeroRepository.delete(pasajero);
        usuarioRepository.delete(pasajero.getUsuario());
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
