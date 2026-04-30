package api.proyecto.redes.repository;

import api.proyecto.redes.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Optional<Administrador> findByUsuario_IdUsuario(Long usuarioId);
    boolean existsByUsuario_IdUsuario(Long usuarioId);
}
