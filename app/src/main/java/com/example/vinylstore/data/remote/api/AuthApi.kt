package com.example.vinylstore.data.remote.api

import com.example.vinylstore.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>
    
    @GET("api/auth/profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: Int): Response<UserProfileDto>
    
    @PUT("api/auth/profile/{userId}")
    suspend fun updateProfile(
        @Path("userId") userId: Int,
        @Body request: UpdateProfileRequest
    ): Response<UserProfileDto>
    
}

