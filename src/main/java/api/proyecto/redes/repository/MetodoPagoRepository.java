package api.proyecto.redes.repository;

import api.proyecto.redes.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {

    /**
     * Obtiene todos los métodos de pago de un usuario
     */
    List<MetodoPago> findByUsuario_IdUsuario(Long usuarioId);

    /**
     * Obtiene el método de pago predeterminado del usuario
     */
    @Query("SELECT m FROM MetodoPago m WHERE m.usuario.idUsuario = :usuarioId AND m.predeterminado = true")
    Optional<MetodoPago> obtenerPredeterminado(@Param("usuarioId") Long usuarioId);

    /**
     * Busca un método de pago por Stripe ID
     */
    Optional<MetodoPago> findByStripePaymentMethodId(String stripePaymentMethodId);
}
