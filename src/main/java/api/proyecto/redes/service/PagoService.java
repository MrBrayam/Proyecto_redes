package api.proyecto.redes.service;

import api.proyecto.redes.dto.MetodoPagoRequest;
import api.proyecto.redes.dto.MetodoPagoResponse;
import api.proyecto.redes.dto.TransaccionResponse;
import api.proyecto.redes.model.EstadoTransaccion;
import api.proyecto.redes.model.MetodoPago;
import api.proyecto.redes.model.Transaccion;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.model.Viaje;
import api.proyecto.redes.repository.MetodoPagoRepository;
import api.proyecto.redes.repository.TransaccionRepository;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.repository.ViajeRepository;
import api.proyecto.redes.util.TarifaCalculadora;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private final TransaccionRepository transaccionRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    public PagoService(TransaccionRepository transaccionRepository,
                      MetodoPagoRepository metodoPagoRepository,
                      ViajeRepository viajeRepository,
                      UsuarioRepository usuarioRepository,
                      NotificacionService notificacionService) {
        this.transaccionRepository = transaccionRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
    }

    // ==================== MÉTODOS DE PAGO ====================

    /**
     * Agrega un nuevo método de pago para un usuario
     */
    public MetodoPagoResponse agregarMetodoPago(Long usuarioId, MetodoPagoRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Si es predeterminado, desmarcar otros
        if (Boolean.TRUE.equals(request.predeterminado())) {
            List<MetodoPago> metodos = metodoPagoRepository.findByUsuario_IdUsuario(usuarioId);
            metodos.forEach(m -> {
                m.setPredeterminado(false);
                metodoPagoRepository.save(m);
            });
        }

        // Extraer últimos 4 dígitos del token (ejemplo: si viene en formato "****1234")
        String ultimos4 = request.stripeToken().length() >= 4 
            ? request.stripeToken().substring(request.stripeToken().length() - 4) 
            : request.stripeToken();

        MetodoPago metodo = new MetodoPago(
            usuario,
            request.tipo(),
            ultimos4,
            request.nombreTitular(),
            request.stripeToken()  // En producción, esto sería el Stripe Payment Method ID
        );
        metodo.setPredeterminado(Boolean.TRUE.equals(request.predeterminado()));

        MetodoPago guardado = metodoPagoRepository.save(metodo);
        return toMetodoPagoResponse(guardado);
    }

    /**
     * Obtiene todos los métodos de pago de un usuario
     */
    public List<MetodoPagoResponse> obtenerMetodosPago(Long usuarioId) {
        List<MetodoPago> metodos = metodoPagoRepository.findByUsuario_IdUsuario(usuarioId);
        return metodos.stream().map(this::toMetodoPagoResponse).toList();
    }

    /**
     * Establece un método de pago como predeterminado
     */
    public MetodoPagoResponse establecerComoPredeterminado(Long metodoPagoId, Long usuarioId) {
        MetodoPago metodo = metodoPagoRepository.findById(metodoPagoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pago no encontrado"));

        if (!metodo.getUsuario().getIdUsuario().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        // Desmarcar otros
        List<MetodoPago> metodos = metodoPagoRepository.findByUsuario_IdUsuario(usuarioId);
        metodos.forEach(m -> {
            m.setPredeterminado(false);
            metodoPagoRepository.save(m);
        });

        // Marcar este como predeterminado
        metodo.setPredeterminado(true);
        MetodoPago guardado = metodoPagoRepository.save(metodo);
        return toMetodoPagoResponse(guardado);
    }

    /**
     * Elimina un método de pago
     */
    public void eliminarMetodoPago(Long metodoPagoId, Long usuarioId) {
        MetodoPago metodo = metodoPagoRepository.findById(metodoPagoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pago no encontrado"));

        if (!metodo.getUsuario().getIdUsuario().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        metodoPagoRepository.delete(metodo);
    }

    // ==================== TRANSACCIONES ====================

    /**
     * Crea una transacción pendiente para un viaje
     * Retorna el client_secret de Stripe para completar el pago en frontend
     */
    public TransaccionResponse crearTransaccionPendiente(Long viajeId, Long usuarioId, Long metodoPagoId) {
        Viaje viaje = viajeRepository.findById(viajeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Viaje no encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        MetodoPago metodo = metodoPagoRepository.findById(metodoPagoId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pago no encontrado"));

        // Validar que el usuario sea el pasajero del viaje
        if (!viaje.getPasajero().getIdUsuario().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para pagar este viaje");
        }

        // Calcular montos
        BigDecimal montoTotal = viaje.getPrecio();
        BigDecimal comision = TarifaCalculadora.calcularComision(montoTotal);
        BigDecimal gananciaConductor = TarifaCalculadora.calcularGananciaConductor(montoTotal);

        // Crear transacción
        Transaccion transaccion = new Transaccion(viaje, usuario, metodo, montoTotal, comision, gananciaConductor);
        transaccion.setDescripcion("Pago por viaje");
        Transaccion guardada = transaccionRepository.save(transaccion);

        return toTransaccionResponse(guardada);
    }

    /**
     * Confirma una transacción después del pago exitoso en Stripe
     */
    public TransaccionResponse confirmarTransaccion(Long transaccionId, String stripePaymentIntentId) {
        Transaccion transaccion = transaccionRepository.findById(transaccionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transacción no encontrada"));

        transaccion.setEstado(EstadoTransaccion.COMPLETADA);
        transaccion.setStripePaymentIntentId(stripePaymentIntentId);
        transaccion.setCompletadoEn(LocalDateTime.now());

        Transaccion guardada = transaccionRepository.save(transaccion);

        // Enviar notificaciones
        notificacionService.enviarNotificacionViaje(
            transaccion.getUsuarioPagador().getIdUsuario(),
            "Pago confirmado",
            "Tu viaje ha sido pagado exitosamente. Monto: S/. " + transaccion.getMonto(),
            api.proyecto.redes.model.TipoNotificacion.PAGO_PROCESADO,
            transaccion.getViaje().getIdViaje()
        );

        // Notificar al conductor
        if (transaccion.getViaje().getConductor() != null) {
            notificacionService.enviarNotificacionViaje(
                transaccion.getViaje().getConductor().getUsuario().getIdUsuario(),
                "Viaje pagado",
                "Ganancia: S/. " + transaccion.getGananciaConductor(),
                api.proyecto.redes.model.TipoNotificacion.PAGO_PROCESADO,
                transaccion.getViaje().getIdViaje()
            );
        }

        return toTransaccionResponse(guardada);
    }

    /**
     * Marca una transacción como fallida
     */
    public TransaccionResponse fallarTransaccion(Long transaccionId, String razonFallo) {
        Transaccion transaccion = transaccionRepository.findById(transaccionId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transacción no encontrada"));

        transaccion.setEstado(EstadoTransaccion.FALLIDA);
        transaccion.setRazonFallo(razonFallo);
        Transaccion guardada = transaccionRepository.save(transaccion);

        // Notificar al usuario
        notificacionService.enviarNotificacionViaje(
            transaccion.getUsuarioPagador().getIdUsuario(),
            "Pago fallido",
            "No se pudo procesar el pago. Razón: " + razonFallo,
            api.proyecto.redes.model.TipoNotificacion.ADVERTENCIA,
            transaccion.getViaje().getIdViaje()
        );

        return toTransaccionResponse(guardada);
    }

    /**
     * Obtiene el historial de transacciones de un usuario
     */
    public List<TransaccionResponse> obtenerHistorialTransacciones(Long usuarioId) {
        List<Transaccion> transacciones = transaccionRepository.obtenerTransaccionesCompletadasPagador(usuarioId);
        return transacciones.stream().map(this::toTransaccionResponse).toList();
    }

    /**
     * Obtiene las ganancias totales de un conductor
     */
    public BigDecimal obtenerGananciasConductor(Long conductorId) {
        return transaccionRepository.obtenerGananciasTotalConductor(conductorId);
    }

    /**
     * Obtiene las comisiones totales de la plataforma
     */
    public BigDecimal obtenerComisionesTotalPlataforma() {
        return transaccionRepository.obtenerComisionesTotalPlataforma();
    }

    // ==================== MAPPERS ====================

    private MetodoPagoResponse toMetodoPagoResponse(MetodoPago metodo) {
        return new MetodoPagoResponse(
            metodo.getIdMetodoPago(),
            metodo.getTipo(),
            metodo.getUltimosCuatroDigitos(),
            metodo.getNombreTitular(),
            metodo.getPredeterminado(),
            metodo.getCreadoEn()
        );
    }

    private TransaccionResponse toTransaccionResponse(Transaccion transaccion) {
        return new TransaccionResponse(
            transaccion.getIdTransaccion(),
            transaccion.getViaje().getIdViaje(),
            transaccion.getMonto(),
            transaccion.getComision(),
            transaccion.getGananciaConductor(),
            transaccion.getEstado(),
            transaccion.getMetodoPago().getTipo().toString(),
            transaccion.getDescripcion(),
            transaccion.getRazonFallo(),
            transaccion.getCreadoEn(),
            transaccion.getCompletadoEn()
        );
    }
}
