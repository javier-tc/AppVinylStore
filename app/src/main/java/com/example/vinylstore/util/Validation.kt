package com.example.vinylstore.util

//lógica de validación centralizada y desacoplada
object Validation {
    
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("El email no puede estar vacío")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> 
                ValidationResult.Error("Ingresa un email válido")
            else -> ValidationResult.Success
        }
    }
    
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Error("La contraseña no puede estar vacía")
            password.length < 6 -> ValidationResult.Error("La contraseña debe tener al menos 6 caracteres")
            else -> ValidationResult.Success
        }
    }
    
    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("El nombre no puede estar vacío")
            name.length < 2 -> ValidationResult.Error("El nombre debe tener al menos 2 caracteres")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

