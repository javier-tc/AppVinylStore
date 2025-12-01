package com.example.vinylstore.repository

import com.example.vinylstore.data.model.*
import com.example.vinylstore.data.remote.dto.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MappersTest {
    
    @Test
    fun `toProduct convierte ProductDto a Product correctamente`() {
        val dto = ProductDto(
            id = 1,
            title = "Test Album",
            artist = "Test Artist",
            price = 29.99,
            description = "Test Description",
            genre = "Rock",
            imageUrl = "https://example.com/image.jpg",
            stock = 10
        )
        
        val product = dto.toProduct()
        
        assertEquals(1, product.id)
        assertEquals("Test Album", product.titulo)
        assertEquals("Test Artist", product.artista)
        assertEquals(29.99, product.precio, 0.01)
        assertEquals("Test Description", product.descripcion)
        assertEquals("Rock", product.genero)
        assertEquals("https://example.com/image.jpg", product.imagenUrl)
        assertEquals(10, product.stock)
    }
    
    @Test
    fun `toCreateProductRequest convierte Product a CreateProductRequest correctamente`() {
        val product = Product(
            id = 1,
            titulo = "Test Album",
            artista = "Test Artist",
            precio = 29.99,
            descripcion = "Test Description",
            genero = "Rock",
            imagenUrl = "https://example.com/image.jpg",
            stock = 10
        )
        
        val request = product.toCreateProductRequest()
        
        assertEquals("Test Album", request.title)
        assertEquals("Test Artist", request.artist)
        assertEquals("Rock", request.genre)
        assertEquals("Test Description", request.description)
        assertEquals(29.99, request.price, 0.01)
        assertEquals(10, request.stock)
        assertEquals("https://example.com/image.jpg", request.imageUrl)
    }
    
    @Test
    fun `toUser convierte UserProfileDto a User correctamente con rol ADMIN`() {
        val dto = UserProfileDto(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            role = "ADMIN"
        )
        
        val user = dto.toUser()
        
        assertEquals(1L, user.id)
        assertEquals("John Doe", user.nombre)
        assertEquals("john@example.com", user.email)
        assertEquals("", user.password)
        assertEquals("administrador", user.rol)
    }
    
    @Test
    fun `toUser convierte UserProfileDto a User correctamente con rol CLIENTE`() {
        val dto = UserProfileDto(
            id = 2,
            firstName = "Jane",
            lastName = "Smith",
            email = "jane@example.com",
            role = "CLIENTE"
        )
        
        val user = dto.toUser()
        
        assertEquals(2L, user.id)
        assertEquals("Jane Smith", user.nombre)
        assertEquals("jane@example.com", user.email)
        assertEquals("", user.password)
        assertEquals("cliente", user.rol)
    }
    
    @Test
    fun `toUser maneja nombre sin apellido correctamente`() {
        val dto = UserProfileDto(
            id = 3,
            firstName = "Single",
            lastName = "",
            email = "single@example.com",
            role = "CLIENTE"
        )
        
        val user = dto.toUser()
        
        assertEquals("Single", user.nombre)
    }
    
    @Test
    fun `toCartItem convierte CartItemDto a CartItem correctamente`() {
        val dto = CartItemDto(
            id = 1,
            productId = 10,
            quantity = 2,
            userId = 5,
            unitPrice = 29.99,
            subtotal = 59.98
        )
        
        val cartItem = dto.toCartItem()
        
        assertEquals(1L, cartItem.id)
        assertEquals(10, cartItem.productId)
        assertEquals(2, cartItem.cantidad)
        assertEquals(5L, cartItem.userId)
    }
    
    @Test
    fun `toOrder convierte OrderResponse a Order correctamente`() {
        val fechaISO = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        val dto = OrderResponse(
            id = 1,
            productId = 10,
            userId = 5,
            cantidad = 2,
            precioUnitario = 29.99,
            total = 59.98,
            fecha = fechaISO,
            estado = "COMPLETADO"
        )
        
        val order = dto.toOrder()
        
        assertEquals(1, order.id)
        assertEquals(10, order.productId)
        assertEquals(5, order.userId)
        assertEquals(2, order.cantidad)
        assertEquals(29.99, order.precioUnitario, 0.01)
        assertEquals(59.98, order.total, 0.01)
        assertEquals("completado", order.estado)
        assertTrue(order.fecha > 0)
    }
    
    @Test
    fun `toOrder normaliza estado a minúsculas`() {
        val fechaISO = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        val dto = OrderResponse(
            id = 1,
            productId = 10,
            userId = 5,
            cantidad = 1,
            precioUnitario = 10.0,
            total = 10.0,
            fecha = fechaISO,
            estado = "PENDIENTE"
        )
        
        val order = dto.toOrder()
        
        assertEquals("pendiente", order.estado)
    }
    
    @Test
    fun `toOrder maneja fecha inválida usando fallback`() {
        val dto = OrderResponse(
            id = 1,
            productId = 10,
            userId = 5,
            cantidad = 1,
            precioUnitario = 10.0,
            total = 10.0,
            fecha = "fecha-invalida",
            estado = "COMPLETADO"
        )
        
        val order = dto.toOrder()
        
        assertTrue(order.fecha > 0) // Debe usar System.currentTimeMillis() como fallback
        assertEquals("completado", order.estado)
    }
}

