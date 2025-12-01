package com.example.vinylstore.ui.util

import com.example.vinylstore.data.model.Product
import org.junit.Assert.*
import org.junit.Test

class ProductUtilsTest {
    
    @Test
    fun `extractCategories debería retornar categorías únicas ordenadas`() {
        val products = listOf(
            Product(1, "Album 1", "Artista 1", 10.0, "Desc", "Rock", "", 5),
            Product(2, "Album 2", "Artista 2", 20.0, "Desc", "Jazz", "", 3),
            Product(3, "Album 3", "Artista 3", 30.0, "Desc", "Rock", "", 2),
            Product(4, "Album 4", "Artista 4", 40.0, "Desc", "Pop", "", 1)
        )
        
        val categories = extractCategories(products)
        
        assertEquals(listOf("Jazz", "Pop", "Rock"), categories)
    }
    
    @Test
    fun `extractCategories debería retornar lista vacía si no hay productos`() {
        val products = emptyList<Product>()
        
        val categories = extractCategories(products)
        
        assertTrue(categories.isEmpty())
    }
    
    @Test
    fun `filterProductsByCategory debería retornar todos los productos cuando category es null`() {
        val products = listOf(
            Product(1, "Album 1", "Artista 1", 10.0, "Desc", "Rock", "", 5),
            Product(2, "Album 2", "Artista 2", 20.0, "Desc", "Jazz", "", 3)
        )
        
        val filtered = filterProductsByCategory(products, null)
        
        assertEquals(products, filtered)
    }
    
    @Test
    fun `filterProductsByCategory debería filtrar por categoría específica`() {
        val products = listOf(
            Product(1, "Album 1", "Artista 1", 10.0, "Desc", "Rock", "", 5),
            Product(2, "Album 2", "Artista 2", 20.0, "Desc", "Jazz", "", 3),
            Product(3, "Album 3", "Artista 3", 30.0, "Desc", "Rock", "", 2)
        )
        
        val filtered = filterProductsByCategory(products, "Rock")
        
        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.genero == "Rock" })
    }
    
    @Test
    fun `filterProductsByCategory debería retornar lista vacía si no hay productos de la categoría`() {
        val products = listOf(
            Product(1, "Album 1", "Artista 1", 10.0, "Desc", "Rock", "", 5)
        )
        
        val filtered = filterProductsByCategory(products, "Jazz")
        
        assertTrue(filtered.isEmpty())
    }
}

