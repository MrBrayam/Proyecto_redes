package api.proyecto.redes.service;

import api.proyecto.redes.model.Administrador;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.repository.AdministradorRepository;
import api.proyecto.redes.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;

    public AdminService(UsuarioRepository usuarioRepository, AdministradorRepository administradorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    public Usuario crear(Usuario usuario) {
        if (usuario.getRol() == null) {
            usuario.setRol(RolUsuario.PASAJERO);
        }
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email es requerido");
        }
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
        }
        Usuario creado = usuarioRepository.save(usuario);
        crearAdministradorSiCorresponde(creado);
        return creado;
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
        if (datos.getRol() != null) {
            existente.setRol(datos.getRol());
        }
        Usuario actualizado = usuarioRepository.save(existente);
        sincronizarAdministrador(actualizado);
        return actualizado;
    }

    public void eliminar(Long id) {
        Usuario existente = obtenerPorId(id);
        administradorRepository.findByUsuario_IdUsuario(existente.getIdUsuario())
            .ifPresent(administradorRepository::delete);
        usuarioRepository.delete(existente);
    }

    private void crearAdministradorSiCorresponde(Usuario usuario) {
        if (usuario.getRol() == RolUsuario.ADMIN
            && !administradorRepository.existsByUsuario_IdUsuario(usuario.getIdUsuario())) {
            Administrador administrador = new Administrador();
            administrador.setUsuario(usuario);
            administradorRepository.save(administrador);
        }
    }

    private void sincronizarAdministrador(Usuario usuario) {
        if (usuario.getRol() == RolUsuario.ADMIN) {
            crearAdministradorSiCorresponde(usuario);
            return;
        }
        administradorRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
            .ifPresent(administradorRepository::delete);
    }
}
