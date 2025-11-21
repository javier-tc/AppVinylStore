package com.example.vinylstore.data.remote.dto

import com.google.gson.annotations.SerializedName

//request DTOs
data class OrderRequest(
    @SerializedName("productId")
    val productId: Int,
    val cantidad: Int,
    @SerializedName("precioUnitario")
    val precioUnitario: Double,
    val estado: String? = null //opcional, por defecto "PENDIENTE" en el backend
)

//response DTOs
data class OrderResponse(
    val id: Long,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("userId")
    val userId: Long,
    val cantidad: Int,
    @SerializedName("precioUnitario")
    val precioUnitario: Double,
    val total: Double,
    val fecha: String, //formato ISO: "2024-01-15T15:00:00"
    val estado: String //"PENDIENTE" o "COMPLETADO"
)

