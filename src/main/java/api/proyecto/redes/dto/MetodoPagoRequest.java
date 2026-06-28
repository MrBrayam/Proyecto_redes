package api.proyecto.redes.dto;

import api.proyecto.redes.model.TipoMetodoPago;

public record MetodoPagoRequest(
    TipoMetodoPago tipo,
    String stripeToken,  // Token de Stripe para el método de pago
    String nombreTitular,
    Boolean predeterminado
) {}
