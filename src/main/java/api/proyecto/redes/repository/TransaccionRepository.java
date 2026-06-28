package api.proyecto.redes.repository;

import api.proyecto.redes.model.Transaccion;
import api.proyecto.redes.model.EstadoTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    /**
     * Obtiene transacciones de un viaje
     */
    List<Transaccion> findByViaje_IdViaje(Long viajeId);

    /**
     * Obtiene la última transacción de un viaje
     */
    @Query("SELECT t FROM Transaccion t WHERE t.viaje.idViaje = :viajeId ORDER BY t.creadoEn DESC LIMIT 1")
    Optional<Transaccion> obtenerUltimaPorViaje(@Param("viajeId") Long viajeId);

    /**
     * Obtiene transacciones completadas de un usuario (como pagador)
     */
    @Query("SELECT t FROM Transaccion t WHERE t.usuarioPagador.idUsuario = :usuarioId AND t.estado = 'COMPLETADA' ORDER BY t.completadoEn DESC")
    List<Transaccion> obtenerTransaccionesCompletadasPagador(@Param("usuarioId") Long usuarioId);

    /**
     * Obtiene transacciones completadas donde el conductor ganó dinero
     */
    @Query("SELECT t FROM Transaccion t WHERE t.viaje.conductor.usuario.idUsuario = :conductorId AND t.estado = 'COMPLETADA' ORDER BY t.completadoEn DESC")
    List<Transaccion> obtenerGananciasConductor(@Param("conductorId") Long conductorId);

    /**
     * Suma las ganancias totales de un conductor
     */
    @Query("SELECT COALESCE(SUM(t.gananciaConductor), 0) FROM Transaccion t WHERE t.viaje.conductor.usuario.idUsuario = :conductorId AND t.estado = 'COMPLETADA'")
    BigDecimal obtenerGananciasTotalConductor(@Param("conductorId") Long conductorId);

    /**
     * Suma las comisiones de la plataforma
     */
    @Query("SELECT COALESCE(SUM(t.comision), 0) FROM Transaccion t WHERE t.estado = 'COMPLETADA'")
    BigDecimal obtenerComisionesTotalPlataforma();

    /**
     * Obtiene transacciones en rango de fechas
     */
    @Query("SELECT t FROM Transaccion t WHERE t.completadoEn BETWEEN :desde AND :hasta AND t.estado = 'COMPLETADA'")
    List<Transaccion> obtenerPorFecha(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    /**
     * Busca por Stripe Payment Intent ID
     */
    Optional<Transaccion> findByStripePaymentIntentId(String stripePaymentIntentId);
}
