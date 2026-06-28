package api.proyecto.redes.repository;

import api.proyecto.redes.model.EstadoViaje;
import api.proyecto.redes.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ViajeRepository extends JpaRepository<Viaje, Long> {
    List<Viaje> findByEstado(EstadoViaje estado);
    List<Viaje> findByPasajero_IdUsuario(Long usuarioId);
    List<Viaje> findByConductor_IdConductor(Long conductorId);
    long countByCreadoEnBetween(LocalDateTime desde, LocalDateTime hasta);

    @Query(value = "select date(creado_en) as etiqueta, count(*) as total "
        + "from viajes where creado_en >= :desde and creado_en < :hasta "
        + "group by date(creado_en) order by date(creado_en)", nativeQuery = true)
    List<SerieConteoRow> conteoPorDia(@Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta);

    @Query(value = "select month(creado_en) as etiqueta, count(*) as total "
        + "from viajes where year(creado_en) = :anio "
        + "group by month(creado_en) order by month(creado_en)", nativeQuery = true)
    List<SerieConteoRow> conteoPorMes(@Param("anio") int anio);

    // Nuevos métodos para Fase 1-4
    @Query("SELECT v FROM Viaje v WHERE v.pasajero.idUsuario = :usuarioId ORDER BY v.creadoEn DESC")
    List<Viaje> obtenerHistorialPasajero(@Param("usuarioId") Long usuarioId);

    @Query("SELECT v FROM Viaje v WHERE v.conductor.idConductor = :conductorId ORDER BY v.creadoEn DESC")
    List<Viaje> obtenerHistorialConductor(@Param("conductorId") Long conductorId);

    @Query("SELECT v FROM Viaje v WHERE v.estado = :estado ORDER BY v.creadoEn DESC")
    List<Viaje> obtenerViajePorEstado(@Param("estado") EstadoViaje estado);

    @Query("SELECT COUNT(v) FROM Viaje v WHERE v.estado = :estado AND v.creadoEn >= :desde AND v.creadoEn <= :hasta")
    long countByEstadoYFecha(@Param("estado") EstadoViaje estado, 
                             @Param("desde") LocalDateTime desde,
                             @Param("hasta") LocalDateTime hasta);

    @Query("SELECT SUM(v.precio) FROM Viaje v WHERE v.conductor.idConductor = :conductorId AND v.estado = :estado")
    Double sumGananciasConductor(@Param("conductorId") Long conductorId, 
                                 @Param("estado") EstadoViaje estado);
}
