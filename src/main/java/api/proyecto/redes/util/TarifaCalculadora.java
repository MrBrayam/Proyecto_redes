package api.proyecto.redes.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculadora de tarifas para viajes
 */
public class TarifaCalculadora {
    
    private static final BigDecimal TARIFA_BASE = new BigDecimal("2.50");      // $2.50 base
    private static final BigDecimal TARIFA_POR_KM = new BigDecimal("1.50");    // $1.50 por km
    private static final BigDecimal TARIFA_POR_MIN = new BigDecimal("0.30");   // $0.30 por minuto
    private static final BigDecimal MULTIPLICADOR_SURGE = new BigDecimal("1.5"); // 1.5x en horas pico
    private static final BigDecimal COMISION_PLATAFORMA = new BigDecimal("0.10"); // 10% comisión

    /**
     * Calcula la tarifa estimada basada en distancia
     * @param distanciaKm distancia en kilómetros
     * @return tarifa estimada sin surge pricing
     */
    public static BigDecimal calcularTarifaBase(double distanciaKm) {
        if (distanciaKm < 0) {
            distanciaKm = 0;
        }
        
        BigDecimal tarifa = TARIFA_BASE
            .add(TARIFA_POR_KM.multiply(BigDecimal.valueOf(distanciaKm)));
        
        return tarifa.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula tarifa con surge pricing (multiplicador por demanda)
     * @param distanciaKm distancia en kilómetros
     * @param multiplicador multiplicador de demanda (ej. 1.0 = sin cambio, 1.5 = 50% más caro)
     * @return tarifa total
     */
    public static BigDecimal calcularTarifaConDemanda(double distanciaKm, BigDecimal multiplicador) {
        BigDecimal tarifaBase = calcularTarifaBase(distanciaKm);
        if (multiplicador == null || multiplicador.compareTo(BigDecimal.ONE) <= 0) {
            return tarifaBase;
        }
        return tarifaBase.multiply(multiplicador).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la ganancia del conductor (después de comisión)
     * @param tarifaTotal tarifa pagada por el pasajero
     * @return ganancia del conductor
     */
    public static BigDecimal calcularGananciaConductor(BigDecimal tarifaTotal) {
        if (tarifaTotal == null || tarifaTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal comision = tarifaTotal.multiply(COMISION_PLATAFORMA);
        return tarifaTotal.subtract(comision).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la comisión de la plataforma
     * @param tarifaTotal tarifa pagada por el pasajero
     * @return comisión de la plataforma
     */
    public static BigDecimal calcularComision(BigDecimal tarifaTotal) {
        if (tarifaTotal == null || tarifaTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return tarifaTotal.multiply(COMISION_PLATAFORMA).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula multa por cancelación
     * @param tarifaTotal tarifa total del viaje
     * @param esPasajero true si cancela pasajero, false si cancela conductor
     * @return monto de la multa
     */
    public static BigDecimal calcularMultaCancelacion(BigDecimal tarifaTotal, boolean esPasajero) {
        if (tarifaTotal == null || tarifaTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // Pasajero: 10%, Conductor: 5%
        BigDecimal porcentaje = esPasajero ? new BigDecimal("0.10") : new BigDecimal("0.05");
        return tarifaTotal.multiply(porcentaje).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal getTarifaBase() {
        return TARIFA_BASE;
    }

    public static BigDecimal getTarifaPorKm() {
        return TARIFA_POR_KM;
    }

    public static BigDecimal getTarifaPorMin() {
        return TARIFA_POR_MIN;
    }
}
