package api.proyecto.redes.dto;

import java.math.BigDecimal;

public record TransaccionRequest(
    Long viajeId,
    Long metodoPagoId,
    String stripePaymentMethodId // Token de pago de Stripe
) {}
