package api.proyecto.redes.repository;

import api.proyecto.redes.model.Cancelacion;
import api.proyecto.redes.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CancelacionRepository extends JpaRepository<Cancelacion, Long> {
    
    List<Cancelacion> findByViaje(Viaje viaje);
}
