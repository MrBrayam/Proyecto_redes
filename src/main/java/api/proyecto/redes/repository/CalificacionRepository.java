package api.proyecto.redes.repository;

import api.proyecto.redes.model.Calificacion;
import api.proyecto.redes.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    
    Optional<Calificacion> findByViaje(Viaje viaje);

    @Query("SELECT c FROM Calificacion c WHERE c.viaje.conductor.idConductor = :conductorId ORDER BY c.creadoEn DESC")
    List<Calificacion> findCalificacionesPorConductor(Long conductorId);

    @Query("SELECT AVG(c.puntuacion) FROM Calificacion c WHERE c.viaje.conductor.idConductor = :conductorId")
    Double calcularCalificacionPromedioPorConductor(Long conductorId);
}
