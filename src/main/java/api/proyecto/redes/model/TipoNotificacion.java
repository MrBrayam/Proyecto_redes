package api.proyecto.redes.model;

public enum TipoNotificacion {
    VIAJE_ACEPTADO,           // Conductor aceptó el viaje
    VIAJE_CANCELADO,          // Viaje fue cancelado
    CONDUCTOR_CERCANO,        // Conductor está llegando (500m)
    CONDUCTOR_LLEGADO,        // Conductor llegó al origen
    SOLICITUD_NUEVA,          // Nueva solicitud para conductor
    VIAJE_FINALIZADO,         // Viaje completado
    PAGO_PROCESADO,           // Pago confirmado
    CALIFICACION_RECIBIDA,    // Recibiste una calificación
    ADVERTENCIA,              // Advertencia del sistema
    INFORMACION               // Información general
}
