package com.example.vinylstore.repository

import com.example.vinylstore.model.User
import com.example.vinylstore.network.SessionManager
import com.example.vinylstore.network.api.AuthApi
import com.example.vinylstore.network.dto.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        //cargar usuario desde sesión si existe
        if (sessionManager.isLoggedIn()) {
            val userId = sessionManager.getUserId()
            if (userId > 0) {
                loadUserProfile(userId)
            }
        }
    }
    
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = authApi.login(request)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                sessionManager.saveToken(authResponse.token)
                sessionManager.saveUserInfo(authResponse.userId, authResponse.email, authResponse.role)
                loadUserProfile(authResponse.userId)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Error al iniciar sesión: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun register(email: String, password: String, firstName: String, lastName: String): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(email, password, firstName, lastName)
            val response = authApi.register(request)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                sessionManager.saveToken(authResponse.token)
                sessionManager.saveUserInfo(authResponse.userId, authResponse.email, authResponse.role)
                loadUserProfile(authResponse.userId)
                Result.success(authResponse)
            } else {
                Result.failure(Exception("Error al registrar usuario: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun logout(): Result<Unit> {
        return try {
            val response = authApi.logout()
            sessionManager.clearSession()
            _currentUser.value = null
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al cerrar sesión"))
            }
        } catch (e: Exception) {
            sessionManager.clearSession()
            _currentUser.value = null
            Result.failure(e)
        }
    }
    
    suspend fun getProfile(userId: Int): Result<UserProfileDto> {
        return try {
            val response = authApi.getProfile(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener perfil: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProfile(userId: Int, firstName: String?, lastName: String?): Result<UserProfileDto> {
        return try {
            val request = UpdateProfileRequest(firstName, lastName)
            val response = authApi.updateProfile(userId, request)
            if (response.isSuccessful && response.body() != null) {
                val profile = response.body()!!
                _currentUser.value = profile.toUser()
                Result.success(profile)
            } else {
                Result.failure(Exception("Error al actualizar perfil: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun loadUserProfile(userId: Int) {
        getProfile(userId).onSuccess { profile ->
            _currentUser.value = profile.toUser()
        }
    }
    
    fun getCurrentUser(): User? {
        return _currentUser.value
    }
}


