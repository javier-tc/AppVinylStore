package com.example.vinylstore.ui.util

import com.example.vinylstore.ui.screens.ShippingInfo

/**
 * Resultado de validación de información de envío
 */
data class ShippingValidationResult(
    val isValid: Boolean,
    val errors: ShippingValidationErrors
)

/**
 * Errores de validación de información de envío
 */
data class ShippingValidationErrors(
    val direccion: String? = null,
    val ciudad: String? = null,
    val codigoPostal: String? = null,
    val telefono: String? = null
)

/**
 * Valida la información de envío
 */
fun validateShippingInfo(
    direccion: String,
    ciudad: String,
    codigoPostal: String,
    telefono: String
): ShippingValidationResult {
    val errors = ShippingValidationErrors()
    var isValid = true
    
    val direccionError = if (direccion.isBlank()) {
        isValid = false
        "La dirección es requerida"
    } else null
    
    val ciudadError = if (ciudad.isBlank()) {
        isValid = false
        "La ciudad es requerida"
    } else null
    
    val codigoPostalError = when {
        codigoPostal.isBlank() -> {
            isValid = false
            "El código postal es requerido"
        }
        !codigoPostal.matches(Regex("\\d{4,6}")) -> {
            isValid = false
            "Código postal inválido"
        }
        else -> null
    }
    
    val telefonoError = when {
        telefono.isBlank() -> {
            isValid = false
            "El teléfono es requerido"
        }
        !telefono.matches(Regex("\\d{8,12}")) -> {
            isValid = false
            "Teléfono inválido"
        }
        else -> null
    }
    
    return ShippingValidationResult(
        isValid = isValid,
        errors = ShippingValidationErrors(
            direccion = direccionError,
            ciudad = ciudadError,
            codigoPostal = codigoPostalError,
            telefono = telefonoError
        )
    )
}

