package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.UserDao
import com.example.vinylstore.model.User
import com.example.vinylstore.util.Validation
import com.example.vinylstore.util.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userDao: UserDao
) : ViewModel() {
    
    init {
        viewModelScope.launch {
            val existingAdmin = userDao.getUserByEmail("admin@vinylstore.com")
            if (existingAdmin == null) {
                val adminUser = User(
                    nombre = "Administrador",
                    email = "admin@vinylstore.com",
                    password = "admin123",
                    rol = "administrador"
                )
                userDao.insertUser(adminUser)
            }
        }
    }
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState
    
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser
    
    fun validateEmail(email: String): ValidationState {
        val result = Validation.validateEmail(email)
        return when (result) {
            is ValidationResult.Success -> ValidationState.Valid
            is ValidationResult.Error -> ValidationState.Invalid(result.message)
        }
    }
    
    fun validatePassword(password: String): ValidationState {
        val result = Validation.validatePassword(password)
        return when (result) {
            is ValidationResult.Success -> ValidationState.Valid
            is ValidationResult.Error -> ValidationState.Invalid(result.message)
        }
    }
    
    fun validateName(name: String): ValidationState {
        val result = Validation.validateName(name)
        return when (result) {
            is ValidationResult.Success -> ValidationState.Valid
            is ValidationResult.Error -> ValidationState.Invalid(result.message)
        }
    }
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            when {
                email.isBlank() -> _loginState.value = LoginState.Error("El email no puede estar vacío")
                password.isBlank() -> _loginState.value = LoginState.Error("La contraseña no puede estar vacía")
                else -> {
                    val user = userDao.getUserByEmail(email)
                    if (user != null && user.password == password) {
                        _currentUser.value = user
                        _loginState.value = LoginState.Success
                    } else {
                        _loginState.value = LoginState.Error("Email o contraseña incorrectos")
                    }
                }
            }
        }
    }
    
    fun register(nombre: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            
            when {
                nombre.isBlank() -> _registerState.value = RegisterState.Error("El nombre no puede estar vacío")
                email.isBlank() -> _registerState.value = RegisterState.Error("El email no puede estar vacío")
                password.isBlank() -> _registerState.value = RegisterState.Error("La contraseña no puede estar vacía")
                else -> {
                    val existingUser = userDao.checkEmailExists(email)
                    if (existingUser > 0) {
                        _registerState.value = RegisterState.Error("Este email ya está registrado")
                    } else {
                        val newUser = User(
                            nombre = nombre,
                            email = email,
                            password = password
                        )
                        val userId = userDao.insertUser(newUser)
                        val createdUser = userDao.getUserById(userId)!!
                        _currentUser.value = createdUser
                        _registerState.value = RegisterState.Success
                    }
                }
            }
        }
    }
    
    fun logout() {
        _currentUser.value = null
        _loginState.value = LoginState.Initial
        _registerState.value = RegisterState.Initial
    }
    
    sealed class LoginState {
        object Initial : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
    
    sealed class RegisterState {
        object Initial : RegisterState()
        object Loading : RegisterState()
        object Success : RegisterState()
        data class Error(val message: String) : RegisterState()
    }
    
    sealed class ValidationState {
        object Valid : ValidationState()
        data class Invalid(val message: String) : ValidationState()
    }
}
