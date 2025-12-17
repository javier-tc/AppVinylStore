package com.example.vinylstore.data.remote.dto

import com.google.gson.annotations.SerializedName

//request DTOs
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String
)

data class UpdateProfileRequest(
    @SerializedName("firstName")
    val firstName: String?,
    @SerializedName("lastName")
    val lastName: String?
)

//response DTOs
data class AuthResponse(
    val token: String,
    val email: String,
    @SerializedName("userId")
    val userId: Int,
    val role: String
)

data class UserProfileDto(
    val id: Int,
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    val role: String
)

data class ErrorResponse(
    val message: String
)

