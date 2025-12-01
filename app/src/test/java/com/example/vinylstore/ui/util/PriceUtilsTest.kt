package com.example.vinylstore.ui.util

import org.junit.Assert.*
import org.junit.Test

class PriceUtilsTest {
    
    @Test
    fun `formatPriceAsInt debería convertir double a int`() {
        assertEquals(10, formatPriceAsInt(10.0))
        assertEquals(10, formatPriceAsInt(10.99))
        assertEquals(0, formatPriceAsInt(0.0))
    }
    
    @Test
    fun `formatPrice debería formatear con símbolo de dólar`() {
        assertEquals("$10", formatPrice(10.0))
        assertEquals("$10", formatPrice(10.99))
        assertEquals("$0", formatPrice(0.0))
        assertEquals("$100", formatPrice(100.5))
    }
    
    @Test
    fun `calculateOrderTotal debería calcular precio por cantidad`() {
        assertEquals(20.0, calculateOrderTotal(10.0, 2), 0.01)
        assertEquals(0.0, calculateOrderTotal(10.0, 0), 0.01)
        assertEquals(50.0, calculateOrderTotal(25.0, 2), 0.01)
    }
    
    @Test
    fun `calculateOrderTotal debería manejar decimales correctamente`() {
        assertEquals(15.75, calculateOrderTotal(5.25, 3), 0.01)
    }
}

