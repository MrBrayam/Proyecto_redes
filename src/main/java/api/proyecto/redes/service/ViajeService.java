package api.proyecto.redes.service;

import api.proyecto.redes.dto.ViajeRequest;
import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.model.EstadoViaje;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.model.Viaje;
import api.proyecto.redes.repository.ViajeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class ViajeService {

    private final ViajeRepository viajeRepository;

    public ViajeService(ViajeRepository viajeRepository) {
        this.viajeRepository = viajeRepository;
    }

    public Viaje crearSolicitud(Usuario pasajero, ViajeRequest request) {
        if (pasajero == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pasajero requerido");
        }
        if (request == null || request.origenLat() == null || request.origenLng() == null
            || request.destinoLat() == null || request.destinoLng() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Origen y destino son requeridos");
        }

        Viaje viaje = new Viaje();
        viaje.setPasajero(pasajero);
        viaje.setOrigenLat(request.origenLat());
        viaje.setOrigenLng(request.origenLng());
        viaje.setDestinoLat(request.destinoLat());
        viaje.setDestinoLng(request.destinoLng());
        viaje.setEstado(EstadoViaje.SOLICITADO);
        return viajeRepository.save(viaje);
    }

    public List<Viaje> listarPendientes() {
        return viajeRepository.findByEstado(EstadoViaje.SOLICITADO);
    }

    public Viaje obtenerPorId(Long id) {
        return viajeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Viaje no encontrado"));
    }

    public Viaje aceptarViaje(Long id, Conductor conductor) {
        Viaje viaje = obtenerPorId(id);
        if (viaje.getEstado() != EstadoViaje.SOLICITADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El viaje ya no esta disponible");
        }
        viaje.setConductor(conductor);
        viaje.setEstado(EstadoViaje.ACEPTADO);
        return viajeRepository.save(viaje);
    }

    public Viaje rechazarViaje(Long id) {
        Viaje viaje = obtenerPorId(id);
        if (viaje.getEstado() != EstadoViaje.SOLICITADO) {
            return viaje;
        }
        viaje.setEstado(EstadoViaje.RECHAZADO);
        return viajeRepository.save(viaje);
    }

    public Viaje finalizarViaje(Long id) {
        Viaje viaje = obtenerPorId(id);
        viaje.setEstado(EstadoViaje.FINALIZADO);
        return viajeRepository.save(viaje);
    }

    public List<Viaje> listarPorPasajero(Long usuarioId) {
        return viajeRepository.findByPasajero_IdUsuario(usuarioId);
    }

    public List<Viaje> listarPorConductor(Long conductorId) {
        return viajeRepository.findByConductor_IdConductor(conductorId);
    }
}
