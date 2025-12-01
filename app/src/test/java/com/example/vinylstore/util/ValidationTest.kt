package com.example.vinylstore.util

import org.junit.Assert.*
import org.junit.Test

class ValidationTest {
    
    @Test
    fun `validateEmail retorna Success para email válido`() {
        val result = Validation.validateEmail("test@example.com")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateEmail retorna Error para email vacío`() {
        val result = Validation.validateEmail("")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El email no puede estar vacío", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateEmail retorna Error para email inválido`() {
        val result = Validation.validateEmail("invalid-email")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Ingresa un email válido", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validatePassword retorna Success para contraseña válida`() {
        val result = Validation.validatePassword("password123")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validatePassword retorna Error para contraseña vacía`() {
        val result = Validation.validatePassword("")
        assertTrue(result is ValidationResult.Error)
        assertEquals("La contraseña no puede estar vacía", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validatePassword retorna Error para contraseña corta`() {
        val result = Validation.validatePassword("12345")
        assertTrue(result is ValidationResult.Error)
        assertEquals("La contraseña debe tener al menos 6 caracteres", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateName retorna Success para nombre válido`() {
        val result = Validation.validateName("Juan")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateName retorna Error para nombre vacío`() {
        val result = Validation.validateName("")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El nombre no puede estar vacío", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateName retorna Error para nombre corto`() {
        val result = Validation.validateName("A")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El nombre debe tener al menos 2 caracteres", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateProductTitle retorna Success para título válido`() {
        val result = Validation.validateProductTitle("Vinilo Clásico")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateProductTitle retorna Error para título vacío`() {
        val result = Validation.validateProductTitle("")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El título no puede estar vacío", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateProductTitle retorna Error para título corto`() {
        val result = Validation.validateProductTitle("A")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El título debe tener al menos 2 caracteres", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateProductArtist retorna Success para artista válido`() {
        val result = Validation.validateProductArtist("The Beatles")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateProductArtist retorna Error para artista vacío`() {
        val result = Validation.validateProductArtist("")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El artista no puede estar vacío", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateProductPrice retorna Success para precio válido`() {
        val result = Validation.validateProductPrice("29.99")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateProductPrice retorna Error para precio vacío`() {
        val result = Validation.validateProductPrice("")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El precio no puede estar vacío", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateProductPrice retorna Error para precio inválido`() {
        val result = Validation.validateProductPrice("abc")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El precio debe ser un número válido", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateProductPrice retorna Error para precio menor o igual a cero`() {
        val result = Validation.validateProductPrice("0")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El precio debe ser mayor a 0", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateProductStock retorna Success para stock vacío`() {
        val result = Validation.validateProductStock("")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateProductStock retorna Success para stock válido`() {
        val result = Validation.validateProductStock("10")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateProductStock retorna Error para stock inválido`() {
        val result = Validation.validateProductStock("abc")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El stock debe ser un número válido", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateProductStock retorna Error para stock negativo`() {
        val result = Validation.validateProductStock("-1")
        assertTrue(result is ValidationResult.Error)
        assertEquals("El stock no puede ser negativo", (result as ValidationResult.Error).message)
    }
    
    @Test
    fun `validateImageUrl retorna Success para URL vacía`() {
        val result = Validation.validateImageUrl("")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateImageUrl retorna Success para URL válida`() {
        val result = Validation.validateImageUrl("https://example.com/image.jpg")
        assertTrue(result is ValidationResult.Success)
    }
    
    @Test
    fun `validateImageUrl retorna Error para URL inválida`() {
        val result = Validation.validateImageUrl("not-a-url")
        assertTrue(result is ValidationResult.Error)
        assertEquals("Ingresa una URL válida", (result as ValidationResult.Error).message)
    }
}

