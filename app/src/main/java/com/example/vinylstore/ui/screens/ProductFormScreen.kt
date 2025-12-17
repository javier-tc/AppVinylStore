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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vinylstore.data.model.Product
import com.example.vinylstore.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    viewModel: ProductViewModel,
    product: Product? = null,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val estado = viewModel.productFormState.collectAsState()
    val saveState = viewModel.saveProductState.collectAsState()
    var hasHandledSuccess by remember(product?.id) { mutableStateOf(false) }
    
    LaunchedEffect(product?.id) {
        viewModel.initializeForm(product)
        hasHandledSuccess = false
    }
    
    LaunchedEffect(saveState.value) {
        val state = saveState.value
        if (state is ProductViewModel.SaveProductState.Success && !hasHandledSuccess) {
            hasHandledSuccess = true
            kotlinx.coroutines.delay(300)
            try {
                viewModel.resetSaveState()
                onSave()
            } catch (e: Exception) {
                //ignorar errores si el composable ya se desmontó
            }
        }
    }
    
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
                    TextButton(
                        onClick = { 
                            val productIdToSave = product?.id
                            if (productIdToSave != null && productIdToSave <= 0) {
                                //mostrar error si el ID es inválido
                            } else {
                                viewModel.saveProduct(productIdToSave)
                            }
                        },
                        enabled = saveState.value !is ProductViewModel.SaveProductState.Loading
                    ) {
                        if (saveState.value is ProductViewModel.SaveProductState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text("Guardar")
                        }
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
                value = estado.value.titulo,
                onValueChange = viewModel::onTituloChange,
                label = { Text("Título") },
                isError = estado.value.errores.titulo != null,
                supportingText = {
                    estado.value.errores.titulo?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            )
            
            OutlinedTextField(
                value = estado.value.artista,
                onValueChange = viewModel::onArtistaChange,
                label = { Text("Artista") },
                isError = estado.value.errores.artista != null,
                supportingText = {
                    estado.value.errores.artista?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            )
            
            OutlinedTextField(
                value = estado.value.precio,
                onValueChange = viewModel::onPrecioChange,
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = estado.value.errores.precio != null,
                supportingText = {
                    estado.value.errores.precio?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            )
            
            OutlinedTextField(
                value = estado.value.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = estado.value.genero,
                onValueChange = viewModel::onGeneroChange,
                label = { Text("Género") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = estado.value.imagenUrl,
                onValueChange = viewModel::onImagenUrlChange,
                label = { Text("URL de Imagen") },
                placeholder = { Text("https://ejemplo.com/imagen.jpg") },
                isError = estado.value.errores.imagenUrl != null,
                supportingText = {
                    estado.value.errores.imagenUrl?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            )
            
            OutlinedTextField(
                value = estado.value.stock,
                onValueChange = viewModel::onStockChange,
                label = { Text("Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = estado.value.errores.stock != null,
                supportingText = {
                    estado.value.errores.stock?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            )
            
            if (saveState.value is ProductViewModel.SaveProductState.Error) {
                Text(
                    text = (saveState.value as ProductViewModel.SaveProductState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

