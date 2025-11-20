package com.example.vinylstore.network.api

import com.example.vinylstore.network.dto.ProductDto
import com.example.vinylstore.network.dto.CreateProductRequest
import com.example.vinylstore.network.dto.UpdateStockRequest
import retrofit2.Response
import retrofit2.http.*

interface ProductApi {
    @GET("api/products")
    suspend fun getProducts(@Query("genero") genero: String? = null): Response<List<ProductDto>>
    
    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Response<ProductDto>
    
    @GET("api/products/search")
    suspend fun searchProducts(@Query("q") query: String): Response<List<ProductDto>>
    
    @POST("api/products")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<ProductDto>
    
    @PUT("api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body request: CreateProductRequest
    ): Response<ProductDto>
    
    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>
    
    @PUT("api/products/{id}/stock")
    suspend fun updateStock(
        @Path("id") id: Int,
        @Body request: UpdateStockRequest
    ): Response<ProductDto>
}


