package com.example.vinylstore.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.vinylstore.viewmodel.CartItemWithProduct
import com.example.vinylstore.viewmodel.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    currentUserId: Long,
    onBack: () -> Unit,
    onConfirmOrder: () -> Unit = {}
) {
    val cartItems by cartViewModel.getCartItems(currentUserId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var totalPrice by remember { mutableStateOf(0.0) }
    
    LaunchedEffect(currentUserId) {
        cartViewModel.loadCart(currentUserId)
    }
    
    LaunchedEffect(cartItems) {
        scope.launch {
            totalPrice = cartViewModel.getTotalPrice(currentUserId)
        }
    }
    
    var isConfirmingOrder by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val handleConfirmOrder: () -> Unit = {
        scope.launch {
            isConfirmingOrder = true
            errorMessage = null
            val result = cartViewModel.confirmOrder(cartItems.map { it.cartItem }, currentUserId)
            isConfirmingOrder = false
            
            result.onSuccess {
                onConfirmOrder() //llama al callback para navegar
            }.onFailure { exception ->
                errorMessage = exception.message ?: "Error al confirmar pedido"
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$${totalPrice.toInt()}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = handleConfirmOrder,
                        modifier = Modifier.height(56.dp),
                        enabled = cartItems.isNotEmpty() && !isConfirmingOrder
                    ) {
                        if (isConfirmingOrder) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Comprar")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tu carrito está vacío",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = errorMessage!!,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = cartItems,
                        key = { it.cartItem.id }
                    ) { cartItemWithProduct ->
                        CartItemCard(
                            cartItemWithProduct = cartItemWithProduct,
                            onUpdateQuantity = { quantity ->
                                cartViewModel.updateCartItemQuantity(
                                    cartItemWithProduct.cartItem,
                                    quantity
                                )
                            },
                            onRemove = {
                                cartViewModel.removeFromCart(cartItemWithProduct.cartItem)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItemWithProduct: CartItemWithProduct,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit
) {
    var isRemoving by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isRemoving) 0f else 1f,
        animationSpec = tween(durationMillis = 300)
    )
    
    if (scale > 0f) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (cartItemWithProduct.product.imagenUrl.isNotEmpty()) {
                        val painter = rememberAsyncImagePainter(
                            model = cartItemWithProduct.product.imagenUrl
                        )
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painter,
                                contentDescription = cartItemWithProduct.product.titulo,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (painter.state is coil.compose.AsyncImagePainter.State.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.Center),
                                    strokeWidth = 2.dp
                                )
                            }
                            if (painter.state is coil.compose.AsyncImagePainter.State.Error) {
                                Text(
                                    text = "Vinilo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Vinilo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cartItemWithProduct.product.titulo,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$${cartItemWithProduct.product.precio.toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (cartItemWithProduct.cartItem.cantidad > 1) {
                                onUpdateQuantity(cartItemWithProduct.cartItem.cantidad - 1)
                            } else {
                                isRemoving = true
                                onRemove()
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Remove,
                            contentDescription = "Disminuir",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = "${cartItemWithProduct.cartItem.cantidad}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.widthIn(min = 20.dp)
                    )
                    
                    IconButton(
                        onClick = {
                            onUpdateQuantity(cartItemWithProduct.cartItem.cantidad + 1)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Aumentar",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                IconButton(
                    onClick = {
                        isRemoving = true
                        onRemove()
                    }
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

