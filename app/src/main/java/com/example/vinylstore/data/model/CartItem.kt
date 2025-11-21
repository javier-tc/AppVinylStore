package com.example.vinylstore.data.model

data class CartItem(
    val id: Long = 0,
    val productId: Int,
    val cantidad: Int,
    val userId: Long
)

