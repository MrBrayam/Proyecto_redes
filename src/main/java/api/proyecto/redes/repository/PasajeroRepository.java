package api.proyecto.redes.repository;

import api.proyecto.redes.model.Pasajero;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasajeroRepository extends JpaRepository<Pasajero, Long> {
    Optional<Pasajero> findByUsuario_IdUsuario(Long usuarioId);
    boolean existsByUsuario_IdUsuario(Long usuarioId);
}
