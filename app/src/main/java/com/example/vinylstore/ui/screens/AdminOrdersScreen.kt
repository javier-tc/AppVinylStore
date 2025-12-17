package com.example.vinylstore.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.vinylstore.ui.util.formatOrderDate
import com.example.vinylstore.ui.util.formatOrderStatus
import com.example.vinylstore.ui.util.getOrderStatusColor
import com.example.vinylstore.viewmodel.OrderViewModel
import com.example.vinylstore.viewmodel.OrderWithProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    orderViewModel: OrderViewModel,
    onBack: () -> Unit
) {
    var allOrders by remember { mutableStateOf<List<OrderWithProduct>>(emptyList()) }
    var isLoadingOrders by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        orderViewModel.refreshAllOrders()
        orderViewModel.getAllOrders().collect { orders ->
            allOrders = orders
            isLoadingOrders = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todas las Órdenes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoadingOrders) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (allOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No hay pedidos",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Las órdenes aparecerán aquí",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allOrders) { orderWithProduct ->
                    AdminOrderCard(orderWithProduct)
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(orderWithProduct: OrderWithProduct) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (orderWithProduct.product != null && orderWithProduct.product.imagenUrl.isNotEmpty()) {
                        val painter = rememberAsyncImagePainter(
                            model = orderWithProduct.product.imagenUrl
                        )
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp)
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = orderWithProduct.product.titulo,
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
                                    text = "V",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = orderWithProduct.product?.titulo ?: "Producto no disponible",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Usuario ID: ${orderWithProduct.order.userId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                StatusChip(orderWithProduct.order.estado)
            }
            
            Text(
                text = orderWithProduct.product?.artista ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Cantidad",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${orderWithProduct.order.cantidad}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column {
                    Text(
                        text = "Precio Unitario",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$${orderWithProduct.order.precioUnitario.toInt()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$${orderWithProduct.order.total.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Text(
                text = formatOrderDate(orderWithProduct.order.fecha),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
