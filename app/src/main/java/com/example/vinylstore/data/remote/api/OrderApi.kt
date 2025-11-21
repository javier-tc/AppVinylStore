package com.example.vinylstore.data.remote.api

import com.example.vinylstore.data.remote.dto.OrderRequest
import com.example.vinylstore.data.remote.dto.OrderResponse
import retrofit2.Response
import retrofit2.http.*

interface OrderApi {
    @GET("api/orders/my-orders")
    suspend fun getMyOrders(): Response<List<OrderResponse>>
    
    @GET("api/orders")
    suspend fun getAllOrders(): Response<List<OrderResponse>>
    
    @GET("api/orders/estado/{estado}")
    suspend fun getOrdersByEstado(@Path("estado") estado: String): Response<List<OrderResponse>>
    
    @POST("api/orders")
    suspend fun createOrder(@Body request: OrderRequest): Response<OrderResponse>
}

