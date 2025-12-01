package com.example.vinylstore.ui.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Formatea una fecha en milisegundos a string con formato dd/MM/yyyy HH:mm
 */
fun formatOrderDate(timestampMillis: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(timestampMillis))
}

/**
 * Obtiene el color asociado a un estado de pedido
 */
fun getOrderStatusColor(estado: String): String {
    return when (estado.lowercase()) {
        "confirmado" -> "primary"
        "pendiente" -> "tertiary"
        "entregado" -> "primaryContainer"
        else -> "error"
    }
}

/**
 * Formatea el estado de un pedido capitalizando la primera letra y el resto en minúsculas
 */
fun formatOrderStatus(estado: String): String {
    return estado.lowercase().replaceFirstChar { it.uppercase() }
}

