package api.proyecto.redes.service;

import api.proyecto.redes.dto.ConductorCercanoResponse;
import api.proyecto.redes.model.Conductor;
import api.proyecto.redes.model.ConductorUbicacion;
import api.proyecto.redes.repository.ConductorRepository;
import api.proyecto.redes.repository.ConductorUbicacionRepository;
import api.proyecto.redes.util.GeoUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConductorUbicacionService {

    private final ConductorUbicacionRepository ubicacionRepository;
    private final ConductorRepository conductorRepository;

    public ConductorUbicacionService(ConductorUbicacionRepository ubicacionRepository,
                                     ConductorRepository conductorRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.conductorRepository = conductorRepository;
    }

    /**
     * Actualiza la ubicación en tiempo real del conductor
     */
    public ConductorUbicacion actualizarUbicacion(Long conductorId, BigDecimal latitud, BigDecimal longitud) {
        Conductor conductor = conductorRepository.findById(conductorId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conductor no encontrado"));

        var ubicacionExistente = ubicacionRepository.findByConductor_IdConductor(conductorId);
        
        ConductorUbicacion ubicacion;
        if (ubicacionExistente.isPresent()) {
            ubicacion = ubicacionExistente.get();
            ubicacion.setLatitud(latitud);
            ubicacion.setLongitud(longitud);
            ubicacion.setActualizadoEn(LocalDateTime.now());
        } else {
            ubicacion = new ConductorUbicacion(conductor, latitud, longitud);
        }

        return ubicacionRepository.save(ubicacion);
    }

    /**
     * Obtiene la ubicación actual del conductor
     */
    public ConductorUbicacion obtenerUbicacion(Long conductorId) {
        return ubicacionRepository.findByConductor_IdConductor(conductorId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ubicación del conductor no encontrada"));
    }

    /**
     * Busca conductores cercanos al punto especificado
     * @param latitud latitud del punto de búsqueda
     * @param longitud longitud del punto de búsqueda
     * @param radioKm radio de búsqueda en kilómetros (por defecto 5 km)
     * @return lista de conductores cercanos ordenados por distancia
     */
    public List<ConductorCercanoResponse> buscarConductoresCercanos(BigDecimal lat, BigDecimal lng, double radio) {
        if (lat == null || lng == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Latitud y longitud requeridas");
        }

        final double radioKm = radio <= 0 ? 5.0 : radio; // Default: 5 km

        // Obtener todos los conductores disponibles con ubicación
        var conductoresConUbicacion = ubicacionRepository.findAll()
            .stream()
            .filter(ub -> ub.getConductor().getDisponible() != null && ub.getConductor().getDisponible())
            .collect(Collectors.toList());

        // Filtrar por radio y calcular distancia
        return conductoresConUbicacion.stream()
            .filter(ub -> GeoUtils.dentroDelRadio(lat, lng, ub.getLatitud(), ub.getLongitud(), radioKm))
            .map(ub -> {
                double distancia = GeoUtils.calcularDistanciaKm(lat, lng, ub.getLatitud(), ub.getLongitud());
                Conductor conductor = ub.getConductor();
                return new ConductorCercanoResponse(
                    conductor.getIdConductor(),
                    conductor.getUsuario().getNombre(),
                    conductor.getVehiculo(),
                    conductor.getCalificacionPromedio() != null ? conductor.getCalificacionPromedio() : BigDecimal.ZERO,
                    true,
                    distancia
                );
            })
            .sorted((a, b) -> Double.compare(a.distanciaKm(), b.distanciaKm()))
            .collect(Collectors.toList());
    }

    /**
     * Verifica si un conductor está disponible (tiene ubicación actualizada recientemente)
     */
    public boolean conductorDisponible(Long conductorId, int minutosTolerancia) {
        try {
            ConductorUbicacion ubicacion = obtenerUbicacion(conductorId);
            LocalDateTime hace = LocalDateTime.now().minusMinutes(minutosTolerancia);
            return ubicacion.getActualizadoEn().isAfter(hace);
        } catch (ResponseStatusException e) {
            return false;
        }
    }
}
