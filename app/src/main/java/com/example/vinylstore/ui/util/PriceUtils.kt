package com.example.vinylstore.ui.util

/**
 * Formatea un precio como entero para mostrar en UI
 */
fun formatPriceAsInt(price: Double): Int {
    return price.toInt()
}

/**
 * Formatea un precio como string con símbolo de dólar
 */
fun formatPrice(price: Double): String {
    return "$${formatPriceAsInt(price)}"
}

/**
 * Calcula el total de un pedido
 */
fun calculateOrderTotal(price: Double, quantity: Int): Double {
    return price * quantity
}

