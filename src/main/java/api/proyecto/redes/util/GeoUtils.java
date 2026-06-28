package api.proyecto.redes.util;

import java.math.BigDecimal;

/**
 * Utilidad para cálculos geográficos usando la fórmula de Haversine
 */
public class GeoUtils {
    private static final double EARTH_RADIUS_KM = 6371.0; // Radio de la tierra en km

    /**
     * Calcula la distancia en kilómetros entre dos puntos geográficos
     * usando la fórmula de Haversine
     */
    public static double calcularDistanciaKm(BigDecimal lat1, BigDecimal lng1, 
                                            BigDecimal lat2, BigDecimal lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return Double.MAX_VALUE;
        }
        
        double latRad1 = Math.toRadians(lat1.doubleValue());
        double latRad2 = Math.toRadians(lat2.doubleValue());
        double deltaLatRad = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLngRad = Math.toRadians(lng2.doubleValue() - lng1.doubleValue());

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                   Math.cos(latRad1) * Math.cos(latRad2) *
                   Math.sin(deltaLngRad / 2) * Math.sin(deltaLngRad / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calcula si dos puntos están dentro de un radio especificado (en km)
     */
    public static boolean dentroDelRadio(BigDecimal lat1, BigDecimal lng1,
                                         BigDecimal lat2, BigDecimal lng2,
                                         double radioKm) {
        double distancia = calcularDistanciaKm(lat1, lng1, lat2, lng2);
        return distancia <= radioKm;
    }
}
