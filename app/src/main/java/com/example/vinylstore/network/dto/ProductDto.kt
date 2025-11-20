package com.example.vinylstore.network.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    val id: Int,
    val title: String,
    val artist: String,
    val genre: String,
    val description: String,
    val price: Double,
    val stock: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?
)

data class CreateProductRequest(
    val title: String,
    val artist: String,
    val genre: String,
    val description: String,
    val price: Double,
    val stock: Int,
    @SerializedName("imageUrl")
    val imageUrl: String
)

data class UpdateStockRequest(
    val stock: Int
)


