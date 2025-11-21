package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.model.User
import com.example.vinylstore.repository.AuthRepository
import com.example.vinylstore.util.Validation
import com.example.vinylstore.util.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState
    
    private val _loginFormState = MutableStateFlow(
        LoginFormState(
            correo = "",
            password = "",
            errores = LoginFormErrors()
        )
    )
    val loginFormState: StateFlow<LoginFormState> = _loginFormState
    
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Initial)
    val registerState: StateFlow<RegisterState> = _registerState
    
    private val _registerFormState = MutableStateFlow(
        RegisterFormState(
            nombre = "",
            apellido = "",
            correo = "",
            password = "",
            errores = RegisterFormErrors()
        )
    )
    val registerFormState: StateFlow<RegisterFormState> = _registerFormState
    
    val currentUser: StateFlow<User?> = authRepository.currentUser
    
    fun onLoginCorreoChange(correo: String) {
        _loginFormState.value = _loginFormState.value.copy(
            correo = correo,
            errores = _loginFormState.value.errores.copy(
                correo = validateEmail(correo).let {
                    when (it) {
                        is ValidationState.Valid -> null
                        is ValidationState.Invalid -> it.message
                    }
                }
            )
        )
    }
    
    fun onLoginPasswordChange(password: String) {
        _loginFormState.value = _loginFormState.value.copy(
            password = password,
            errores = _loginFormState.value.errores.copy(
                password = validatePassword(password).let {
                    when (it) {
                        is ValidationState.Valid -> null
                        is ValidationState.Invalid -> it.message
                    }
                }
            )
        )
    }
    
    fun onNombreChange(nombre: String) {
        _registerFormState.value = _registerFormState.value.copy(
            nombre = nombre,
            errores = _registerFormState.value.errores.copy(
                nombre = validateName(nombre).let {
                    when (it) {
                        is ValidationState.Valid -> null
                        is ValidationState.Invalid -> it.message
                    }
                }
            )
        )
    }
    
    fun onApellidoChange(apellido: String) {
        _registerFormState.value = _registerFormState.value.copy(
            apellido = apellido,
            errores = _registerFormState.value.errores.copy(
                apellido = validateName(apellido).let {
                    when (it) {
                        is ValidationState.Valid -> null
                        is ValidationState.Invalid -> it.message
                    }
                }
            )
        )
    }
    
    fun onCorreoChange(correo: String) {
        _registerFormState.value = _registerFormState.value.copy(
            correo = correo,
            errores = _registerFormState.value.errores.copy(
                correo = validateEmail(correo).let {
                    when (it) {
                        is ValidationState.Valid -> null
                        is ValidationState.Invalid -> it.message
                    }
                }
            )
        )
    }
    
    fun onPasswordChange(password: String) {
        _registerFormState.value = _registerFormState.value.copy(
            password = password,
            errores = _registerFormState.value.errores.copy(
                password = validatePassword(password).let {
                    when (it) {
                        is ValidationState.Valid -> null
                        is ValidationState.Invalid -> it.message
                    }
                }
            )
        )
    }
    
    private fun validateEmail(email: String): ValidationState {
        val result = Validation.validateEmail(email)
        return when (result) {
            is ValidationResult.Success -> ValidationState.Valid
            is ValidationResult.Error -> ValidationState.Invalid(result.message)
        }
    }
    
    private fun validatePassword(password: String): ValidationState {
        val result = Validation.validatePassword(password)
        return when (result) {
            is ValidationResult.Success -> ValidationState.Valid
            is ValidationResult.Error -> ValidationState.Invalid(result.message)
        }
    }
    
    private fun validateName(name: String): ValidationState {
        val result = Validation.validateName(name)
        return when (result) {
            is ValidationResult.Success -> ValidationState.Valid
            is ValidationResult.Error -> ValidationState.Invalid(result.message)
        }
    }
    
    fun login() {
        viewModelScope.launch {
            val estado = _loginFormState.value
            
            //validar todos los campos antes de iniciar sesión
            val correoValidation = validateEmail(estado.correo)
            val passwordValidation = validatePassword(estado.password)
            
            val nuevosErrores = LoginFormErrors(
                correo = when (correoValidation) {
                    is ValidationState.Valid -> null
                    is ValidationState.Invalid -> correoValidation.message
                },
                password = when (passwordValidation) {
                    is ValidationState.Valid -> null
                    is ValidationState.Invalid -> passwordValidation.message
                }
            )
            
            _loginFormState.value = estado.copy(errores = nuevosErrores)
            
            //si hay errores, no continuar
            if (nuevosErrores.correo != null || nuevosErrores.password != null) {
                return@launch
            }
            
            _loginState.value = LoginState.Loading
            
            authRepository.login(estado.correo, estado.password)
                .onSuccess {
                    _loginState.value = LoginState.Success
                }
                .onFailure { exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "Error al iniciar sesión")
                }
        }
    }
    
    fun register() {
        viewModelScope.launch {
            val estado = _registerFormState.value
            
            //validar todos los campos antes de registrar
            val nombreValidation = validateName(estado.nombre)
            val apellidoValidation = validateName(estado.apellido)
            val correoValidation = validateEmail(estado.correo)
            val passwordValidation = validatePassword(estado.password)
            
            val nuevosErrores = RegisterFormErrors(
                nombre = when (nombreValidation) {
                    is ValidationState.Valid -> null
                    is ValidationState.Invalid -> nombreValidation.message
                },
                apellido = when (apellidoValidation) {
                    is ValidationState.Valid -> null
                    is ValidationState.Invalid -> apellidoValidation.message
                },
                correo = when (correoValidation) {
                    is ValidationState.Valid -> null
                    is ValidationState.Invalid -> correoValidation.message
                },
                password = when (passwordValidation) {
                    is ValidationState.Valid -> null
                    is ValidationState.Invalid -> passwordValidation.message
                }
            )
            
            _registerFormState.value = estado.copy(errores = nuevosErrores)
            
            //si hay errores, no continuar
            if (nuevosErrores.nombre != null || nuevosErrores.apellido != null || nuevosErrores.correo != null || nuevosErrores.password != null) {
                return@launch
            }
            
            _registerState.value = RegisterState.Loading
            
            authRepository.register(estado.correo, estado.password, estado.nombre.trim(), estado.apellido.trim())
                .onSuccess {
                    _registerState.value = RegisterState.Success
                }
                .onFailure { exception ->
                    _registerState.value = RegisterState.Error(exception.message ?: "Error al registrar usuario")
                }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _loginState.value = LoginState.Initial
            _loginFormState.value = LoginFormState(
                correo = "",
                password = "",
                errores = LoginFormErrors()
            )
            _registerState.value = RegisterState.Initial
            _registerFormState.value = RegisterFormState(
                nombre = "",
                apellido = "",
                correo = "",
                password = "",
                errores = RegisterFormErrors()
            )
        }
    }
    
    data class LoginFormState(
        val correo: String,
        val password: String,
        val errores: LoginFormErrors
    )
    
    data class LoginFormErrors(
        val correo: String? = null,
        val password: String? = null
    )
    
    data class RegisterFormState(
        val nombre: String,
        val apellido: String,
        val correo: String,
        val password: String,
        val errores: RegisterFormErrors
    )
    
    data class RegisterFormErrors(
        val nombre: String? = null,
        val apellido: String? = null,
        val correo: String? = null,
        val password: String? = null
    )
    
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
