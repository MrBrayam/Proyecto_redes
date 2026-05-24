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
}
