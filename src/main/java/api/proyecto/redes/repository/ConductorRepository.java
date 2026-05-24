package api.proyecto.redes.repository;

import api.proyecto.redes.model.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConductorRepository extends JpaRepository<Conductor, Long> {
    List<Conductor> findByDisponible(Boolean disponible);
    Optional<Conductor> findByUsuario_IdUsuario(Long usuarioId);
}
