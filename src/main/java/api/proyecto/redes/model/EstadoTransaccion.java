package api.proyecto.redes.model;

public enum EstadoTransaccion {
    PENDIENTE,      // En espera de procesamiento
    PROCESANDO,     // Enviado a Stripe
    COMPLETADA,     // Pago exitoso
    FALLIDA,        // Pago rechazado
    REEMBOLSADA     // Dinero devuelto
}
