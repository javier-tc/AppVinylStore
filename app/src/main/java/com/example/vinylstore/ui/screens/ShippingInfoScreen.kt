package com.example.vinylstore.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vinylstore.ui.util.validateShippingInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingInfoScreen(
    onBack: () -> Unit,
    onConfirm: (ShippingInfo) -> Unit
) {
    var direccion by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var codigoPostal by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var instrucciones by remember { mutableStateOf("") }
    
    var direccionError by remember { mutableStateOf<String?>(null) }
    var ciudadError by remember { mutableStateOf<String?>(null) }
    var codigoPostalError by remember { mutableStateOf<String?>(null) }
    var telefonoError by remember { mutableStateOf<String?>(null) }
    
    fun validateForm(): Boolean {
        val result = validateShippingInfo(direccion, ciudad, codigoPostal, telefono)
        direccionError = result.errors.direccion
        ciudadError = result.errors.ciudad
        codigoPostalError = result.errors.codigoPostal
        telefonoError = result.errors.telefono
        return result.isValid
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Datos de Envío") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ingresa los datos de envío para tu pedido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            OutlinedTextField(
                value = direccion,
                onValueChange = {
                    direccion = it
                    direccionError = null
                },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = direccionError != null,
                supportingText = direccionError?.let { { Text(it) } }
            )
            
            OutlinedTextField(
                value = ciudad,
                onValueChange = {
                    ciudad = it
                    ciudadError = null
                },
                label = { Text("Ciudad") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = ciudadError != null,
                supportingText = ciudadError?.let { { Text(it) } }
            )
            
            OutlinedTextField(
                value = codigoPostal,
                onValueChange = {
                    codigoPostal = it
                    codigoPostalError = null
                },
                label = { Text("Código Postal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = codigoPostalError != null,
                supportingText = codigoPostalError?.let { { Text(it) } }
            )
            
            OutlinedTextField(
                value = telefono,
                onValueChange = {
                    telefono = it
                    telefonoError = null
                },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = telefonoError != null,
                supportingText = telefonoError?.let { { Text(it) } }
            )
            
            OutlinedTextField(
                value = instrucciones,
                onValueChange = { instrucciones = it },
                label = { Text("Instrucciones de entrega (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (validateForm()) {
                        onConfirm(
                            ShippingInfo(
                                direccion = direccion.trim(),
                                ciudad = ciudad.trim(),
                                codigoPostal = codigoPostal.trim(),
                                telefono = telefono.trim(),
                                instrucciones = instrucciones.trim()
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar Envío", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

data class ShippingInfo(
    val direccion: String,
    val ciudad: String,
    val codigoPostal: String,
    val telefono: String,
    val instrucciones: String = ""
)


