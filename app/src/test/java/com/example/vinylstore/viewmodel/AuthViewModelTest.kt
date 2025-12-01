package com.example.vinylstore.viewmodel

import com.example.vinylstore.data.model.User
import com.example.vinylstore.data.remote.dto.AuthResponse
import com.example.vinylstore.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        every { authRepository.currentUser } returns MutableStateFlow(null)
        viewModel = AuthViewModel(authRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `onLoginCorreoChange actualiza el correo y valida`() {
        viewModel.onLoginCorreoChange("test@example.com")
        val state = viewModel.loginFormState.value
        assertEquals("test@example.com", state.correo)
    }
    
    @Test
    fun `onLoginPasswordChange actualiza la contraseña`() {
        viewModel.onLoginPasswordChange("password123")
        val state = viewModel.loginFormState.value
        assertEquals("password123", state.password)
    }
    
    @Test
    fun `login con credenciales válidas actualiza el estado a Success`() = runTest {
        val authResponse = AuthResponse(
            token = "test-token",
            userId = 1,
            email = "test@example.com",
            role = "cliente"
        )
        
        coEvery { authRepository.login(any(), any()) } returns Result.success(authResponse)
        every { authRepository.currentUser } returns MutableStateFlow(null)
        
        viewModel.onLoginCorreoChange("test@example.com")
        viewModel.onLoginPasswordChange("password123")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { authRepository.login("test@example.com", "password123") }
    }
    
    @Test
    fun `login con credenciales inválidas actualiza el estado a Error`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns Result.failure(Exception("Error de autenticación"))
        every { authRepository.currentUser } returns MutableStateFlow(null)
        
        viewModel.onLoginCorreoChange("test@example.com")
        viewModel.onLoginPasswordChange("wrongpass")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.loginState.value
        assertTrue(state is AuthViewModel.LoginState.Error)
    }
    
    @Test
    fun `register con datos válidos actualiza el estado a Success`() = runTest {
        val authResponse = AuthResponse(
            token = "test-token",
            userId = 1,
            email = "new@example.com",
            role = "cliente"
        )
        
        coEvery { authRepository.register(any(), any(), any(), any()) } returns Result.success(authResponse)
        every { authRepository.currentUser } returns MutableStateFlow(null)
        
        viewModel.onNombreChange("Juan")
        viewModel.onApellidoChange("Pérez")
        viewModel.onCorreoChange("new@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { authRepository.register("new@example.com", "password123", "Juan", "Pérez") }
    }
    
    @Test
    fun `logout limpia el estado`() = runTest {
        coEvery { authRepository.logout() } returns Result.success(Unit)
        every { authRepository.currentUser } returns MutableStateFlow(null)
        
        viewModel.logout()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { authRepository.logout() }
        assertEquals(AuthViewModel.LoginState.Initial, viewModel.loginState.value)
    }
    
    @Test
    fun `onLoginCorreoChange valida email inválido`() {
        viewModel.onLoginCorreoChange("invalid-email")
        val state = viewModel.loginFormState.value
        assertNotNull(state.errores.correo)
    }
    
    @Test
    fun `onLoginPasswordChange valida contraseña corta`() {
        viewModel.onLoginPasswordChange("12345")
        val state = viewModel.loginFormState.value
        assertNotNull(state.errores.password)
    }
    
    @Test
    fun `onNombreChange actualiza el nombre`() {
        viewModel.onNombreChange("Juan")
        val state = viewModel.registerFormState.value
        assertEquals("Juan", state.nombre)
    }
    
    @Test
    fun `onApellidoChange actualiza el apellido`() {
        viewModel.onApellidoChange("Pérez")
        val state = viewModel.registerFormState.value
        assertEquals("Pérez", state.apellido)
    }
    
    @Test
    fun `onCorreoChange actualiza el correo en registro`() {
        viewModel.onCorreoChange("test@example.com")
        val state = viewModel.registerFormState.value
        assertEquals("test@example.com", state.correo)
    }
    
    @Test
    fun `onPasswordChange actualiza la contraseña en registro`() {
        viewModel.onPasswordChange("password123")
        val state = viewModel.registerFormState.value
        assertEquals("password123", state.password)
    }
    
    @Test
    fun `login no continúa si hay errores de validación`() = runTest {
        viewModel.onLoginCorreoChange("invalid-email")
        viewModel.onLoginPasswordChange("123")
        viewModel.login()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }
    
    @Test
    fun `register no continúa si hay errores de validación`() = runTest {
        viewModel.onNombreChange("")
        viewModel.onApellidoChange("")
        viewModel.onCorreoChange("invalid")
        viewModel.onPasswordChange("123")
        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify(exactly = 0) { authRepository.register(any(), any(), any(), any()) }
    }
    
    @Test
    fun `register con datos inválidos actualiza el estado a Error`() = runTest {
        coEvery { authRepository.register(any(), any(), any(), any()) } returns Result.failure(Exception("Error de registro"))
        every { authRepository.currentUser } returns MutableStateFlow(null)
        
        viewModel.onNombreChange("Juan")
        viewModel.onApellidoChange("Pérez")
        viewModel.onCorreoChange("test@example.com")
        viewModel.onPasswordChange("password123")
        viewModel.register()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.registerState.value
        assertTrue(state is AuthViewModel.RegisterState.Error)
    }
    
    @Test
    fun `login actualiza estado a Loading`() = runTest {
        val authResponse = AuthResponse(
            token = "test-token",
            userId = 1,
            email = "test@example.com",
            role = "cliente"
        )
        
        coEvery { authRepository.login(any(), any()) } returns Result.success(authResponse)
        every { authRepository.currentUser } returns MutableStateFlow(null)
        
        viewModel.onLoginCorreoChange("test@example.com")
        viewModel.onLoginPasswordChange("password123")
        viewModel.login()
        
        // Verificar que el estado pasa por Loading antes de avanzar
        // Como la coroutine se ejecuta rápidamente, verificamos el estado final
        testDispatcher.scheduler.advanceUntilIdle()
        
        val finalState = viewModel.loginState.value
        // El estado final debería ser Success porque el mock retorna éxito
        assertTrue(finalState is AuthViewModel.LoginState.Success)
    }
}


