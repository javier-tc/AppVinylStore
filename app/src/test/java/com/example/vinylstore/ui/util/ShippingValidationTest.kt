package com.example.vinylstore.ui.util

import org.junit.Assert.*
import org.junit.Test

class ShippingValidationTest {
    
    @Test
    fun `validateShippingInfo debería retornar válido cuando todos los campos son correctos`() {
        val result = validateShippingInfo(
            direccion = "Calle 123",
            ciudad = "Bogotá",
            codigoPostal = "110111",
            telefono = "3001234567"
        )
        
        assertTrue(result.isValid)
        assertNull(result.errors.direccion)
        assertNull(result.errors.ciudad)
        assertNull(result.errors.codigoPostal)
        assertNull(result.errors.telefono)
    }
    
    @Test
    fun `validateShippingInfo debería retornar error cuando dirección está vacía`() {
        val result = validateShippingInfo(
            direccion = "",
            ciudad = "Bogotá",
            codigoPostal = "110111",
            telefono = "3001234567"
        )
        
        assertFalse(result.isValid)
        assertEquals("La dirección es requerida", result.errors.direccion)
    }
    
    @Test
    fun `validateShippingInfo debería retornar error cuando ciudad está vacía`() {
        val result = validateShippingInfo(
            direccion = "Calle 123",
            ciudad = "",
            codigoPostal = "110111",
            telefono = "3001234567"
        )
        
        assertFalse(result.isValid)
        assertEquals("La ciudad es requerida", result.errors.ciudad)
    }
    
    @Test
    fun `validateShippingInfo debería retornar error cuando código postal está vacío`() {
        val result = validateShippingInfo(
            direccion = "Calle 123",
            ciudad = "Bogotá",
            codigoPostal = "",
            telefono = "3001234567"
        )
        
        assertFalse(result.isValid)
        assertEquals("El código postal es requerido", result.errors.codigoPostal)
    }
    
    @Test
    fun `validateShippingInfo debería retornar error cuando código postal no tiene formato válido`() {
        val result = validateShippingInfo(
            direccion = "Calle 123",
            ciudad = "Bogotá",
            codigoPostal = "ABC123",
            telefono = "3001234567"
        )
        
        assertFalse(result.isValid)
        assertEquals("Código postal inválido", result.errors.codigoPostal)
    }
    
    @Test
    fun `validateShippingInfo debería aceptar código postal con 4 dígitos`() {
        val result = validateShippingInfo(
            direccion = "Calle 123",
            ciudad = "Bogotá",
            codigoPostal = "1234",
            telefono = "3001234567"
        )
        
        assertTrue(result.isValid)
        assertNull(result.errors.codigoPostal)
    }
    
    @Test
    fun `validateShippingInfo debería aceptar código postal con 6 dígitos`() {
        val result = validateShippingInfo(
            direccion = "Calle 123",
            ciudad = "Bogotá",
            codigoPostal = "123456",
            telefono = "3001234567"
        )
        
        assertTrue(result.isValid)
        assertNull(result.errors.codigoPostal)
    }
    
    @Test
    fun `validateShippingInfo debería retornar error cuando teléfono está vacío`() {
        val result = validateShippingInfo(
            direccion = "Calle 123",
            ciudad = "Bogotá",
            codigoPostal = "110111",
            telefono = ""
        )
        
        assertFalse(result.isValid)
        assertEquals("El teléfono es requerido", result.errors.telefono)
    }
    
    @Test
    fun `validateShippingInfo debería retornar error cuando teléfono no tiene formato válido`() {
        val result = validateShippingInfo(
            direccion = "Calle 123",
            ciudad = "Bogotá",
            codigoPostal = "110111",
            telefono = "ABC123"
        )
        
        assertFalse(result.isValid)
        assertEquals("Teléfono inválido", result.errors.telefono)
    }
    
    @Test
    fun `validateShippingInfo debería aceptar teléfono con 8 dígitos`() {
        val result = validateShippingInfo(
            direccion = "Calle 123",
            ciudad = "Bogotá",
            codigoPostal = "110111",
            telefono = "12345678"
        )
        
        assertTrue(result.isValid)
        assertNull(result.errors.telefono)
    }
    
    @Test
    fun `validateShippingInfo debería aceptar teléfono con 12 dígitos`() {
        val result = validateShippingInfo(
            direccion = "Calle 123",
            ciudad = "Bogotá",
            codigoPostal = "110111",
            telefono = "123456789012"
        )
        
        assertTrue(result.isValid)
        assertNull(result.errors.telefono)
    }
    
    @Test
    fun `validateShippingInfo debería retornar múltiples errores cuando varios campos son inválidos`() {
        val result = validateShippingInfo(
            direccion = "",
            ciudad = "",
            codigoPostal = "ABC",
            telefono = "123"
        )
        
        assertFalse(result.isValid)
        assertNotNull(result.errors.direccion)
        assertNotNull(result.errors.ciudad)
        assertNotNull(result.errors.codigoPostal)
        assertNotNull(result.errors.telefono)
    }
}

