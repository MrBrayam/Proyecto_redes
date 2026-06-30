package api.proyecto.redes.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TarifaCalculadoraTests {

    @Test
    void calcularTarifaBase_debeIncluirTarifaBaseMasDistancia() {
        BigDecimal tarifa = TarifaCalculadora.calcularTarifaBase(10.0);
        assertEquals(new BigDecimal("17.50"), tarifa);
    }

    @Test
    void calcularComisionYGanancia_debenSerConsistentes() {
        BigDecimal total = new BigDecimal("100.00");
        BigDecimal comision = TarifaCalculadora.calcularComision(total);
        BigDecimal ganancia = TarifaCalculadora.calcularGananciaConductor(total);

        assertEquals(new BigDecimal("10.00"), comision);
        assertEquals(new BigDecimal("90.00"), ganancia);
    }
}
