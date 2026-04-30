package api.proyecto.redes.repository;

import api.proyecto.redes.model.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {
    List<Conductor> findByDisponible(Boolean disponible);
}
