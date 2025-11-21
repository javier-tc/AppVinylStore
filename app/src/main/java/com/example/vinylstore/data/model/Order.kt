package com.example.vinylstore.data.model

data class Order(
    val id: Long = 0,
    val productId: Int,
    val userId: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val total: Double,
    val fecha: Long = System.currentTimeMillis(),
    val estado: String = "confirmado" //"pendiente", "confirmado", "entregado"
)

