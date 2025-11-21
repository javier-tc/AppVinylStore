package com.example.vinylstore.data.remote.api

import com.example.vinylstore.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CartApi {
    @GET("api/cart/{userId}")
    suspend fun getCart(@Path("userId") userId: Int): Response<List<CartItemDto>>
    
    @POST("api/cart/{userId}/items")
    suspend fun addItem(
        @Path("userId") userId: Int,
        @Body request: AddCartItemRequest
    ): Response<CartItemDto>
    
    @PUT("api/cart/{userId}/items/{itemId}")
    suspend fun updateItem(
        @Path("userId") userId: Int,
        @Path("itemId") itemId: Int,
        @Body request: UpdateCartItemRequest
    ): Response<CartItemDto>
    
    @DELETE("api/cart/{userId}/items/{itemId}")
    suspend fun deleteItem(
        @Path("userId") userId: Int,
        @Path("itemId") itemId: Int
    ): Response<Unit>
    
    @DELETE("api/cart/{userId}")
    suspend fun clearCart(@Path("userId") userId: Int): Response<Unit>
    
    @GET("api/cart/{userId}/total")
    suspend fun getCartTotal(@Path("userId") userId: Int): Response<CartTotalResponse>
}

