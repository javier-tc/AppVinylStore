package com.example.vinylstore.ui.util

import com.example.vinylstore.data.model.Product
import org.junit.Assert.*
import org.junit.Test

class ProductDetailUtilsTest {
    
    @Test
    fun `validateProductQuantity debería retornar 1 si quantity es menor a 1`() {
        val product = Product(1, "Test", "Artist", 10.0, "Desc", "Rock", "", 5)
        
        val result = validateProductQuantity(0, product)
        
        assertEquals(1, result)
    }
    
    @Test
    fun `validateProductQuantity debería retornar stock si quantity es mayor a stock`() {
        val product = Product(1, "Test", "Artist", 10.0, "Desc", "Rock", "", 5)
        
        val result = validateProductQuantity(10, product)
        
        assertEquals(5, result)
    }
    
    @Test
    fun `validateProductQuantity debería retornar quantity si está en rango válido`() {
        val product = Product(1, "Test", "Artist", 10.0, "Desc", "Rock", "", 5)
        
        val result = validateProductQuantity(3, product)
        
        assertEquals(3, result)
    }
    
    @Test
    fun `canIncrementQuantity debería retornar true si quantity es menor a stock`() {
        val product = Product(1, "Test", "Artist", 10.0, "Desc", "Rock", "", 5)
        
        assertTrue(canIncrementQuantity(3, product))
    }
    
    @Test
    fun `canIncrementQuantity debería retornar false si quantity es igual a stock`() {
        val product = Product(1, "Test", "Artist", 10.0, "Desc", "Rock", "", 5)
        
        assertFalse(canIncrementQuantity(5, product))
    }
    
    @Test
    fun `canIncrementQuantity debería retornar false si quantity es mayor a stock`() {
        val product = Product(1, "Test", "Artist", 10.0, "Desc", "Rock", "", 5)
        
        assertFalse(canIncrementQuantity(6, product))
    }
    
    @Test
    fun `canDecrementQuantity debería retornar true si quantity es mayor a 1`() {
        assertTrue(canDecrementQuantity(2))
        assertTrue(canDecrementQuantity(5))
    }
    
    @Test
    fun `canDecrementQuantity debería retornar false si quantity es 1`() {
        assertFalse(canDecrementQuantity(1))
    }
}

