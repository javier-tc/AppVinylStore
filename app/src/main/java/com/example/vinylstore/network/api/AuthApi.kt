package com.example.vinylstore.network.api

import com.example.vinylstore.network.dto.*
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
    
    @GET("api/auth/users")
    suspend fun getUsers(): Response<List<UserProfileDto>>
    
    @PUT("api/auth/users/{userId}/role")
    suspend fun updateUserRole(
        @Path("userId") userId: Int,
        @Body request: UpdateRoleRequest
    ): Response<UserProfileDto>
}


