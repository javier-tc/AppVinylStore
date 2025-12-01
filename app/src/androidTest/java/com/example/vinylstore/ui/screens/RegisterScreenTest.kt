package com.example.vinylstore.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.junit4.createComposeRule
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

class RegisterScreenTest {
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
    fun registerScreen_muestraCamposDeEntrada() {
        val authRepository = createFakeAuthRepository()
        val viewModel = AuthViewModel(authRepository)
        
        composeTestRule.setContent {
            VinylStoreTheme {
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {},
                    onNavigateToLogin = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Nombre").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apellido").assertIsDisplayed()
        composeTestRule.onNodeWithText("Correo electrónico").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
    }
    
    @Test
    fun registerScreen_muestraBotonRegistrarse() {
        val authRepository = createFakeAuthRepository()
        val viewModel = AuthViewModel(authRepository)
        
        composeTestRule.setContent {
            VinylStoreTheme {
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {},
                    onNavigateToLogin = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Registrarse").assertIsDisplayed()
        composeTestRule.onNodeWithText("Registrarse").assertIsEnabled()
    }
    
    @Test
    fun registerScreen_muestraBotonLogin() {
        val authRepository = createFakeAuthRepository()
        val viewModel = AuthViewModel(authRepository)
        var navigateToLoginCalled = false
        
        composeTestRule.setContent {
            VinylStoreTheme {
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {},
                    onNavigateToLogin = { navigateToLoginCalled = true }
                )
            }
        }
        
        composeTestRule.onNodeWithText("¿Ya tienes cuenta? Inicia sesión").assertIsDisplayed()
        composeTestRule.onNodeWithText("¿Ya tienes cuenta? Inicia sesión").performClick()
        
        assert(navigateToLoginCalled)
    }
    
    @Test
    fun registerScreen_permiteIngresarDatos() {
        val authRepository = createFakeAuthRepository()
        val viewModel = AuthViewModel(authRepository)
        
        composeTestRule.setContent {
            VinylStoreTheme {
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {},
                    onNavigateToLogin = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Nombre").performTextInput("Juan")
        composeTestRule.onNodeWithText("Apellido").performTextInput("Pérez")
        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("juan@example.com")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
    }
}

