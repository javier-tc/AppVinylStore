package com.example.vinylstore.ui.util

import com.example.vinylstore.data.model.Product

/**
 * Valida y ajusta la cantidad de un producto según el stock disponible
 */
fun validateProductQuantity(quantity: Int, product: Product): Int {
    return when {
        quantity < 1 -> 1
        quantity > product.stock -> product.stock
        else -> quantity
    }
}

/**
 * Verifica si se puede incrementar la cantidad
 */
fun canIncrementQuantity(quantity: Int, product: Product): Boolean {
    return quantity < product.stock
}

/**
 * Verifica si se puede decrementar la cantidad
 */
fun canDecrementQuantity(quantity: Int): Boolean {
    return quantity > 1
}

