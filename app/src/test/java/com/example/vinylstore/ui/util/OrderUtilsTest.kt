package com.example.vinylstore.ui.util

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class OrderUtilsTest {
    
    @Test
    fun `formatOrderDate debería formatear fecha correctamente`() {
        val timestamp = 1609459200000L // 01/01/2021 00:00:00 UTC
        
        val formatted = formatOrderDate(timestamp)
        
        assertTrue(formatted.matches(Regex("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}")))
    }
    
    @Test
    fun `getOrderStatusColor debería retornar primary para confirmado`() {
        assertEquals("primary", getOrderStatusColor("confirmado"))
        assertEquals("primary", getOrderStatusColor("CONFIRMADO"))
        assertEquals("primary", getOrderStatusColor("Confirmado"))
    }
    
    @Test
    fun `getOrderStatusColor debería retornar tertiary para pendiente`() {
        assertEquals("tertiary", getOrderStatusColor("pendiente"))
        assertEquals("tertiary", getOrderStatusColor("PENDIENTE"))
    }
    
    @Test
    fun `getOrderStatusColor debería retornar primaryContainer para entregado`() {
        assertEquals("primaryContainer", getOrderStatusColor("entregado"))
        assertEquals("primaryContainer", getOrderStatusColor("ENTREGADO"))
    }
    
    @Test
    fun `getOrderStatusColor debería retornar error para estado desconocido`() {
        assertEquals("error", getOrderStatusColor("desconocido"))
        assertEquals("error", getOrderStatusColor(""))
    }
    
    @Test
    fun `formatOrderStatus debería capitalizar primera letra`() {
        assertEquals("Confirmado", formatOrderStatus("confirmado"))
        assertEquals("Pendiente", formatOrderStatus("pendiente"))
        assertEquals("Entregado", formatOrderStatus("entregado"))
    }
    
    @Test
    fun `formatOrderStatus debería mantener mayúsculas después de la primera letra`() {
        assertEquals("Confirmado", formatOrderStatus("CONFIRMADO"))
    }
}

