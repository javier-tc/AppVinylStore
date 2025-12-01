package com.example.vinylstore.repository

import com.example.vinylstore.data.remote.api.AuthApi
import com.example.vinylstore.data.remote.dto.AuthResponse
import com.example.vinylstore.data.remote.dto.UserProfileDto
import com.example.vinylstore.data.remote.SessionManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import okhttp3.ResponseBody as OkHttpResponseBody

class AuthRepositoryTest {
    private lateinit var authApi: AuthApi
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: AuthRepository
    
    @Before
    fun setup() {
        authApi = mockk()
        sessionManager = mockk(relaxed = true)
        repository = AuthRepository(authApi, sessionManager)
    }
    
    @Test
    fun `login exitoso guarda token y actualiza usuario`() = runTest {
        val authResponse = AuthResponse("token123", "test@example.com", 1, "cliente")
        val response = Response.success(authResponse)
        val profileResponse = Response.success(
            UserProfileDto(1, "test@example.com", "Juan", "Pérez", "cliente")
        )
        
        coEvery { authApi.login(any()) } returns response
        coEvery { authApi.getProfile(any()) } returns profileResponse
        
        val result = repository.login("test@example.com", "password123")
        
        assertTrue(result.isSuccess)
        verify { sessionManager.saveToken("token123") }
        verify { sessionManager.saveUserInfo(1, "test@example.com", "cliente") }
    }
    
    @Test
    fun `login fallido retorna error`() = runTest {
        val response = Response.error<AuthResponse>(400, OkHttpResponseBody.create(null, ""))
        coEvery { authApi.login(any()) } returns response
        
        val result = repository.login("test@example.com", "wrong")
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `register exitoso crea nuevo usuario`() = runTest {
        val authResponse = AuthResponse("token123", "new@example.com", 2, "cliente")
        val response = Response.success(authResponse)
        val profileResponse = Response.success(
            UserProfileDto(2, "new@example.com", "María", "García", "cliente")
        )
        
        coEvery { authApi.register(any()) } returns response
        coEvery { authApi.getProfile(any()) } returns profileResponse
        
        val result = repository.register("new@example.com", "password123", "María", "García")
        
        assertTrue(result.isSuccess)
        verify { sessionManager.saveToken("token123") }
    }
    
    @Test
    fun `logout limpia la sesión`() = runTest {
        val response = Response.success<Unit>(Unit)
        coEvery { authApi.logout() } returns response
        
        val result = repository.logout()
        
        assertTrue(result.isSuccess)
        verify { sessionManager.clearSession() }
    }
    
    @Test
    fun `login maneja timeout`() = runTest {
        coEvery { authApi.login(any()) } throws java.net.SocketTimeoutException()
        
        val result = repository.login("test@example.com", "password")
        
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Timeout") == true)
    }
    
    @Test
    fun `login maneja error de conexión`() = runTest {
        coEvery { authApi.login(any()) } throws java.net.UnknownHostException()
        
        val result = repository.login("test@example.com", "password")
        
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("conexión") == true)
    }
    
    @Test
    fun `getProfile retorna perfil exitosamente`() = runTest {
        val profile = UserProfileDto(1, "test@example.com", "Juan", "Pérez", "cliente")
        val response = Response.success(profile)
        coEvery { authApi.getProfile(any()) } returns response
        
        val result = repository.getProfile(1)
        
        assertTrue(result.isSuccess)
        assertEquals("Juan", result.getOrNull()?.firstName)
    }
    
    @Test
    fun `updateProfile actualiza perfil exitosamente`() = runTest {
        val profile = UserProfileDto(1, "test@example.com", "Juan Carlos", "Pérez", "cliente")
        val response = Response.success(profile)
        coEvery { authApi.updateProfile(any(), any()) } returns response
        
        val result = repository.updateProfile(1, "Juan Carlos", null)
        
        assertTrue(result.isSuccess)
        assertEquals("Juan Carlos", result.getOrNull()?.firstName)
    }
    
    @Test
    fun `register maneja error de respuesta`() = runTest {
        val response = Response.error<AuthResponse>(400, OkHttpResponseBody.create(null, ""))
        coEvery { authApi.register(any()) } returns response
        
        val result = repository.register("test@example.com", "password", "Juan", "Pérez")
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `logout maneja error pero limpia sesión`() = runTest {
        val response = Response.error<Unit>(500, OkHttpResponseBody.create(null, ""))
        coEvery { authApi.logout() } returns response
        
        val result = repository.logout()
        
        assertTrue(result.isFailure)
        verify { sessionManager.clearSession() }
    }
}


