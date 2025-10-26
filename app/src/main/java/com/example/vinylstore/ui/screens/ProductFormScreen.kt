package com.example.vinylstore.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.vinylstore.model.Product
import com.example.vinylstore.util.ValidationResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    product: Product? = null,
    onSave: (Product) -> Unit,
    onCancel: () -> Unit
) {
    var titulo by remember { mutableStateOf(product?.titulo ?: "") }
    var artista by remember { mutableStateOf(product?.artista ?: "") }
    var precio by remember { mutableStateOf(product?.precio?.toString() ?: "") }
    var descripcion by remember { mutableStateOf(product?.descripcion ?: "") }
    var genero by remember { mutableStateOf(product?.genero ?: "") }
    var stock by remember { mutableStateOf(product?.stock?.toString() ?: "") }
    
    var errores by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (product == null) "Nuevo Producto" else "Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Cancelar")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        errores = emptyMap()
                        
                        if (titulo.isBlank()) {
                            errores = errores + ("titulo" to "El título no puede estar vacío")
                            return@TextButton
                        }
                        if (artista.isBlank()) {
                            errores = errores + ("artista" to "El artista no puede estar vacío")
                            return@TextButton
                        }
                        val precioValor = precio.toDoubleOrNull()
                        if (precioValor == null || precioValor <= 0) {
                            errores = errores + ("precio" to "El precio debe ser mayor a 0")
                            return@TextButton
                        }
                        val stockValor = stock.toIntOrNull() ?: 0
                        
                        val newProduct = Product(
                            id = product?.id ?: (System.currentTimeMillis().toInt() % 100000),
                            titulo = titulo,
                            artista = artista,
                            precio = precioValor,
                            descripcion = descripcion,
                            genero = genero,
                            imagenUrl = "https://placeholder.com",
                            stock = stockValor
                        )
                        onSave(newProduct)
                    }) {
                        Text("Guardar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it; errores = errores - "titulo" },
                label = { Text("Título") },
                isError = errores.containsKey("titulo"),
                modifier = Modifier.fillMaxWidth().animateContentSize()
            )
            if (errores.containsKey("titulo")) {
                Text(
                    text = errores["titulo"]!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            OutlinedTextField(
                value = artista,
                onValueChange = { artista = it; errores = errores - "artista" },
                label = { Text("Artista") },
                isError = errores.containsKey("artista"),
                modifier = Modifier.fillMaxWidth().animateContentSize()
            )
            if (errores.containsKey("artista")) {
                Text(
                    text = errores["artista"]!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it; errores = errores - "precio" },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = errores.containsKey("precio"),
                modifier = Modifier.fillMaxWidth().animateContentSize()
            )
            if (errores.containsKey("precio")) {
                Text(
                    text = errores["precio"]!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = genero,
                onValueChange = { genero = it },
                label = { Text("Género") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

