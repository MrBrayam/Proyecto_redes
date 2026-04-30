package api.proyecto.redes.service;

import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.repository.ConductorRepository;
import api.proyecto.redes.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ConductorService {

    private final ConductorRepository conductorRepository;
    private final UsuarioRepository usuarioRepository;

    public ConductorService(ConductorRepository conductorRepository, UsuarioRepository usuarioRepository) {
        this.conductorRepository = conductorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Conductor> listar(Boolean disponible) {
        if (disponible == null) {
            return conductorRepository.findAll();
        }
        return conductorRepository.findByDisponible(disponible);
    }

    public Conductor obtenerPorId(Long id) {
        return conductorRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conductor no encontrado"));
    }

    public Conductor crear(Conductor conductor, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no existe"));

        if (usuario.getRol() != RolUsuario.CONDUCTOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no tiene rol CONDUCTOR");
        }

        if (conductor.getDisponible() == null) {
            conductor.setDisponible(true);
        }

        conductor.setUsuario(usuario);
        return conductorRepository.save(conductor);
    }

    public Conductor actualizar(Long id, Conductor datos, Long usuarioId) {
        Conductor existente = obtenerPorId(id);

        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario no existe"));
            if (usuario.getRol() != RolUsuario.CONDUCTOR) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario no tiene rol CONDUCTOR");
            }
            existente.setUsuario(usuario);
        }

        if (datos.getLicencia() != null && !datos.getLicencia().isBlank()) {
            existente.setLicencia(datos.getLicencia());
        }
        if (datos.getVehiculo() != null && !datos.getVehiculo().isBlank()) {
            existente.setVehiculo(datos.getVehiculo());
        }
        if (datos.getCalificacionPromedio() != null) {
            existente.setCalificacionPromedio(datos.getCalificacionPromedio());
        }
        if (datos.getDisponible() != null) {
            existente.setDisponible(datos.getDisponible());
        }

        return conductorRepository.save(existente);
    }

    public void eliminar(Long id) {
        Conductor existente = obtenerPorId(id);
        conductorRepository.delete(existente);
    }
}
