package com.example.vinylstore.ui.util

import com.example.vinylstore.data.model.Product

/**
 * Extrae las categorías únicas de una lista de productos y las ordena alfabéticamente
 */
fun extractCategories(products: List<Product>): List<String> {
    return products.map { it.genero }.distinct().sorted()
}

/**
 * Filtra productos por categoría. Si la categoría es null, retorna todos los productos.
 */
fun filterProductsByCategory(products: List<Product>, category: String?): List<Product> {
    return if (category == null) {
        products
    } else {
        products.filter { it.genero == category }
    }
}

