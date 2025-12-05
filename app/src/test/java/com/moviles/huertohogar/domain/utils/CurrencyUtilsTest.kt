package com.moviles.huertohogar.domain.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyUtilsTest {

    @Test
    fun `formato de cero debe ser $0`() {
        val resultado = CurrencyUtils.formatChileanPeso(0.0)
        // El espacio entre $ y 0 depende de la configuración regional,
        // a veces es "$0" y a veces "$ 0". Verificamos que contenga ambos.
        assert(resultado.contains("$") && resultado.contains("0"))
    }

    @Test
    fun `formato de mil debe tener punto separador`() {
        val resultado = CurrencyUtils.formatChileanPeso(1000.0)
        // Debería ser algo como "$1.000"
        assert(resultado.contains("1.000"))
    }

    @Test
    fun `formato de millon debe tener dos puntos`() {
        val resultado = CurrencyUtils.formatChileanPeso(1000000.0)
        // Debería ser "$1.000.000"
        assert(resultado.contains("1.000.000"))
    }

    @Test
    fun `decimales deben ser ignorados`() {
        val resultado = CurrencyUtils.formatChileanPeso(1500.99)
        // Debería redondear o truncar visualmente a 1.501 o 1.500 dependiendo de la implementación interna de Java
        // Pero lo importante es que NO muestre coma decimal
        assert(!resultado.contains(","))
    }
}