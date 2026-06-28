package api.proyecto.redes.repository;

import api.proyecto.redes.model.ConductorUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConductorUbicacionRepository extends JpaRepository<ConductorUbicacion, Long> {
    
    Optional<ConductorUbicacion> findByConductor_IdConductor(Long conductorId);

    @Query("SELECT cu FROM ConductorUbicacion cu WHERE cu.conductor.idConductor = :conductorId")
    Optional<ConductorUbicacion> obtenerUbicacion(Long conductorId);
}
