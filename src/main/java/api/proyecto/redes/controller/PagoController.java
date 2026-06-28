package api.proyecto.redes.controller;

import api.proyecto.redes.dto.MetodoPagoRequest;
import api.proyecto.redes.dto.MetodoPagoResponse;
import api.proyecto.redes.dto.TransaccionResponse;
import api.proyecto.redes.model.Usuario;
import api.proyecto.redes.repository.UsuarioRepository;
import api.proyecto.redes.service.AuthService;
import api.proyecto.redes.service.PagoService;
import api.proyecto.redes.util.AuthTokenExtractor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    public PagoController(PagoService pagoService,
                         AuthService authService,
                         UsuarioRepository usuarioRepository) {
        this.pagoService = pagoService;
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
    }

    // ==================== MÉTODOS DE PAGO ====================

    /**
     * Agrega un nuevo método de pago
     */
    @PostMapping("/metodos-pago")
    public Map<String, Object> agregarMetodoPago(@RequestBody MetodoPagoRequest request,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        MetodoPagoResponse metodo = pagoService.agregarMetodoPago(usuario.getIdUsuario(), request);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Método de pago agregado exitosamente");
        response.put("metodoPago", metodo);
        return response;
    }

    /**
     * Obtiene todos los métodos de pago del usuario
     */
    @GetMapping("/metodos-pago")
    public Map<String, Object> obtenerMetodosPago(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        List<MetodoPagoResponse> metodos = pagoService.obtenerMetodosPago(usuario.getIdUsuario());

        Map<String, Object> response = new HashMap<>();
        response.put("total", metodos.size());
        response.put("metodosPago", metodos);
        return response;
    }

    /**
     * Establece un método de pago como predeterminado
     */
    @PutMapping("/metodos-pago/{id}/predeterminado")
    public Map<String, Object> establecerComoPredeterminado(@PathVariable Long id,
                                                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        MetodoPagoResponse metodo = pagoService.establecerComoPredeterminado(id, usuario.getIdUsuario());

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Método de pago establecido como predeterminado");
        response.put("metodoPago", metodo);
        return response;
    }

    /**
     * Elimina un método de pago
     */
    @DeleteMapping("/metodos-pago/{id}")
    public Map<String, Object> eliminarMetodoPago(@PathVariable Long id,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        pagoService.eliminarMetodoPago(id, usuario.getIdUsuario());

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Método de pago eliminado");
        return response;
    }

    // ==================== TRANSACCIONES ====================

    /**
     * Crea una transacción pendiente para un viaje
     * Retorna información para que el frontend complete el pago con Stripe
     */
    @PostMapping("/crear-intento")
    public Map<String, Object> crearIntentoTransaccion(@RequestParam Long viajeId,
                                                       @RequestParam Long metodoPagoId,
                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        TransaccionResponse transaccion = pagoService.crearTransaccionPendiente(viajeId, usuario.getIdUsuario(), metodoPagoId);

        Map<String, Object> response = new HashMap<>();
        response.put("transaccionId", transaccion.idTransaccion());
        response.put("monto", transaccion.monto());
        response.put("comision", transaccion.comision());
        response.put("gananciaConductor", transaccion.gananciaConductor());
        response.put("estado", transaccion.estado());
        // En producción, aquí iría el client_secret de Stripe
        response.put("clientSecret", "pi_test_" + transaccion.idTransaccion());
        response.put("mensaje", "Intento de transacción creado. Completa el pago en Stripe.");
        return response;
    }

    /**
     * Confirma una transacción después del pago exitoso
     */
    @PostMapping("/confirmar")
    public Map<String, Object> confirmarTransaccion(@RequestParam Long transaccionId,
                                                    @RequestParam String stripePaymentIntentId,
                                                    @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        validarYObtenerUsuario(authHeader);
        TransaccionResponse transaccion = pagoService.confirmarTransaccion(transaccionId, stripePaymentIntentId);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Transacción completada exitosamente");
        response.put("transaccion", transaccion);
        return response;
    }

    /**
     * Registra una transacción fallida
     */
    @PostMapping("/fallar")
    public Map<String, Object> fallarTransaccion(@RequestParam Long transaccionId,
                                                @RequestParam String razonFallo,
                                                @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        validarYObtenerUsuario(authHeader);
        TransaccionResponse transaccion = pagoService.fallarTransaccion(transaccionId, razonFallo);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Transacción marcada como fallida");
        response.put("transaccion", transaccion);
        return response;
    }

    /**
     * Obtiene el historial de transacciones del usuario
     */
    @GetMapping("/historial")
    public Map<String, Object> obtenerHistorial(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        List<TransaccionResponse> transacciones = pagoService.obtenerHistorialTransacciones(usuario.getIdUsuario());

        Map<String, Object> response = new HashMap<>();
        response.put("total", transacciones.size());
        response.put("transacciones", transacciones);
        return response;
    }

    /**
     * Obtiene las ganancias totales de un conductor
     */
    @GetMapping("/conductor/{conductorId}/ganancias")
    public Map<String, Object> obtenerGananciasConductor(@PathVariable Long conductorId,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        validarYObtenerUsuario(authHeader);
        BigDecimal ganancias = pagoService.obtenerGananciasConductor(conductorId);

        Map<String, Object> response = new HashMap<>();
        response.put("conductorId", conductorId);
        response.put("gananciasTotales", ganancias);
        return response;
    }

    /**
     * Obtiene las comisiones totales de la plataforma (ADMIN ONLY)
     */
    @GetMapping("/admin/comisiones-totales")
    public Map<String, Object> obtenerComisionesTotal(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        Usuario usuario = validarYObtenerUsuario(authHeader);
        // Validar que sea admin
        if (usuario.getRol().toString().equals("ADMIN")) {
            BigDecimal comisiones = pagoService.obtenerComisionesTotalPlataforma();

            Map<String, Object> response = new HashMap<>();
            response.put("comisionesTotales", comisiones);
            return response;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo administradores pueden ver las comisiones totales");
        }
    }

    // ==================== MÉTODOS PRIVADOS ===================

    private Usuario validarYObtenerUsuario(String authHeader) {
        String token = AuthTokenExtractor.extraerToken(authHeader, null);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no proporcionado");
        }

        var auth = authService.obtenerSesion(token);
        if (auth == null || auth.usuario() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        return usuarioRepository.findById(auth.usuario().idUsuario())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}
