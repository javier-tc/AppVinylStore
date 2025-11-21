package com.example.vinylstore.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CartItemDto(
    val id: Int,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("productId")
    val productId: Int,
    val quantity: Int,
    @SerializedName("unitPrice")
    val unitPrice: Double,
    val subtotal: Double
)

data class AddCartItemRequest(
    @SerializedName("productId")
    val productId: Int,
    val quantity: Int
)

data class UpdateCartItemRequest(
    val quantity: Int
)

data class CartTotalResponse(
    @SerializedName("userId")
    val userId: Int,
    val items: List<CartItemDto>,
    val total: Double,
    @SerializedName("totalItems")
    val totalItems: Int
)

