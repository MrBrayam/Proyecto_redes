package api.proyecto.redes.repository;

import api.proyecto.redes.model.Notificacion;
import api.proyecto.redes.model.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    /**
     * Obtiene las últimas notificaciones de un usuario
     */
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = :usuarioId ORDER BY n.creadoEn DESC LIMIT :limite")
    List<Notificacion> obtenerUltimas(@Param("usuarioId") Long usuarioId, @Param("limite") int limite);

    /**
     * Obtiene notificaciones no leídas
     */
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = :usuarioId AND n.leida = false ORDER BY n.creadoEn DESC")
    List<Notificacion> obtenerNoLeidas(@Param("usuarioId") Long usuarioId);

    /**
     * Cuenta notificaciones no leídas
     */
    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.usuario.idUsuario = :usuarioId AND n.leida = false")
    long contarNoLeidas(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene notificaciones por tipo
     */
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = :usuarioId AND n.tipo = :tipo ORDER BY n.creadoEn DESC")
    List<Notificacion> obtenerPorTipo(@Param("usuarioId") Long usuarioId, @Param("tipo") TipoNotificacion tipo);

    /**
     * Obtiene notificaciones relacionadas a un viaje
     */
    @Query("SELECT n FROM Notificacion n WHERE n.usuario.idUsuario = :usuarioId AND n.idViaje = :viajeId ORDER BY n.creadoEn DESC")
    List<Notificacion> obtenerPorViaje(@Param("usuarioId") Long usuarioId, @Param("viajeId") Long viajeId);
}
