package com.example.vinylstore.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import com.example.vinylstore.data.remote.SessionManager
import com.example.vinylstore.data.remote.api.AuthApi
import com.example.vinylstore.data.remote.dto.AuthResponse
import com.example.vinylstore.data.remote.dto.UserProfileDto
import com.example.vinylstore.repository.AuthRepository
import com.example.vinylstore.ui.theme.VinylStoreTheme
import com.example.vinylstore.viewmodel.AuthViewModel
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class LoginScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private fun createFakeAuthRepository(): AuthRepository {
        val fakeApi = object : AuthApi {
            override suspend fun register(request: com.example.vinylstore.data.remote.dto.RegisterRequest) = Response.success(AuthResponse("token", request.email, 1, "cliente"))
            override suspend fun login(request: com.example.vinylstore.data.remote.dto.LoginRequest) = Response.success(AuthResponse("token", request.email, 1, "cliente"))
            override suspend fun logout() = Response.success(Unit)
            override suspend fun getProfile(userId: Int) = Response.success(UserProfileDto(userId, "test@example.com", "Juan", "Pérez", "cliente"))
            override suspend fun updateProfile(userId: Int, request: com.example.vinylstore.data.remote.dto.UpdateProfileRequest) = Response.success(UserProfileDto(userId, "test@example.com", "Juan", "Pérez", "cliente"))
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sessionManager = SessionManager(context)
        return AuthRepository(fakeApi, sessionManager)
    }
    
    @Test
    fun loginScreen_muestraTitulo() {
        val authRepository = createFakeAuthRepository()
        val viewModel = AuthViewModel(authRepository)
        var loginSuccessCalled = false
        var navigateToRegisterCalled = false
        
        composeTestRule.setContent {
            VinylStoreTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = { loginSuccessCalled = true },
                    onNavigateToRegister = { navigateToRegisterCalled = true }
                )
            }
        }
        
        composeTestRule.onNodeWithText("VinylStore").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_muestraCamposDeEntrada() {
        val authRepository = createFakeAuthRepository()
        val viewModel = AuthViewModel(authRepository)
        
        composeTestRule.setContent {
            VinylStoreTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Correo electrónico").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
    }
    
    @Test
    fun loginScreen_muestraBotonIniciarSesion() {
        val authRepository = createFakeAuthRepository()
        val viewModel = AuthViewModel(authRepository)
        
        composeTestRule.setContent {
            VinylStoreTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsEnabled()
    }
    
    @Test
    fun loginScreen_muestraBotonRegistro() {
        val authRepository = createFakeAuthRepository()
        val viewModel = AuthViewModel(authRepository)
        var navigateToRegisterCalled = false
        
        composeTestRule.setContent {
            VinylStoreTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onNavigateToRegister = { navigateToRegisterCalled = true }
                )
            }
        }
        
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate").assertIsDisplayed()
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate").performClick()
        
        // Verificar que se llamó la función de navegación
        assert(navigateToRegisterCalled)
    }
    
    @Test
    fun loginScreen_permiteIngresarCorreo() {
        val authRepository = createFakeAuthRepository()
        val viewModel = AuthViewModel(authRepository)
        
        composeTestRule.setContent {
            VinylStoreTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }
        
        // Buscar el campo de correo y escribir
        composeTestRule.onNodeWithText("Correo electrónico")
            .performTextInput("test@example.com")
    }
    
    @Test
    fun loginScreen_permiteIngresarPassword() {
        val authRepository = createFakeAuthRepository()
        val viewModel = AuthViewModel(authRepository)
        
        composeTestRule.setContent {
            VinylStoreTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }
        
        // Buscar el campo de contraseña y escribir
        composeTestRule.onNodeWithText("Contraseña")
            .performTextInput("password123")
    }
}

