package api.proyecto.redes.repository;

import api.proyecto.redes.model.RolUsuario;
import api.proyecto.redes.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(RolUsuario rol);
    long countByCreadoEnBetween(LocalDateTime desde, LocalDateTime hasta);
    long countByRolAndCreadoEnBetween(RolUsuario rol, LocalDateTime desde, LocalDateTime hasta);
}
