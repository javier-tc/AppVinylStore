package com.example.vinylstore.util

import java.util.regex.Pattern

//lógica de validación centralizada y desacoplada
object Validation {
    
    private val EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    
    private val URL_PATTERN = Pattern.compile(
        "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$",
        Pattern.CASE_INSENSITIVE
    )
    
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Error("El email no puede estar vacío")
            !EMAIL_PATTERN.matcher(email).matches() -> 
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
    
    fun validateProductTitle(titulo: String): ValidationResult {
        return when {
            titulo.isBlank() -> ValidationResult.Error("El título no puede estar vacío")
            titulo.length < 2 -> ValidationResult.Error("El título debe tener al menos 2 caracteres")
            else -> ValidationResult.Success
        }
    }
    
    fun validateProductArtist(artista: String): ValidationResult {
        return when {
            artista.isBlank() -> ValidationResult.Error("El artista no puede estar vacío")
            artista.length < 2 -> ValidationResult.Error("El artista debe tener al menos 2 caracteres")
            else -> ValidationResult.Success
        }
    }
    
    fun validateProductPrice(precio: String): ValidationResult {
        return when {
            precio.isBlank() -> ValidationResult.Error("El precio no puede estar vacío")
            else -> {
                val precioValor = precio.toDoubleOrNull()
                when {
                    precioValor == null -> ValidationResult.Error("El precio debe ser un número válido")
                    precioValor <= 0 -> ValidationResult.Error("El precio debe ser mayor a 0")
                    else -> ValidationResult.Success
                }
            }
        }
    }
    
    fun validateProductStock(stock: String): ValidationResult {
        return when {
            stock.isBlank() -> ValidationResult.Success
            else -> {
                val stockValor = stock.toIntOrNull()
                when {
                    stockValor == null -> ValidationResult.Error("El stock debe ser un número válido")
                    stockValor < 0 -> ValidationResult.Error("El stock no puede ser negativo")
                    else -> ValidationResult.Success
                }
            }
        }
    }
    
    fun validateImageUrl(url: String): ValidationResult {
        return when {
            url.isBlank() -> ValidationResult.Success
            !URL_PATTERN.matcher(url).matches() -> 
                ValidationResult.Error("Ingresa una URL válida")
            else -> ValidationResult.Success
        }
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}

