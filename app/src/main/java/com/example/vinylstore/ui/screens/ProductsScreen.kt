package com.example.vinylstore.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.vinylstore.model.Product
import com.example.vinylstore.viewmodel.CartViewModel
import com.example.vinylstore.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ProductViewModel,
    cartViewModel: CartViewModel,
    currentUserId: Long,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (Product) -> Unit
) {
    val products by viewModel.products.collectAsState(initial = emptyList())
    val cartItemCount by cartViewModel.getCartItemCount(currentUserId).collectAsState(initial = 0)
    var addedProductId by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()
    
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    val categories = remember(products) {
        products.map { it.genero }.distinct().sorted()
    }
    
    val filteredProducts = remember(products, selectedCategory) {
        if (selectedCategory == null) {
            products
        } else {
            products.filter { it.genero == selectedCategory }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VinylStore") },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge {
                                        Text("$cartItemCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = "Carrito")
                        }
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            item {
                Text(
                    text = "Productos Destacados",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(products.take(5)) { product ->
                        ProductCarouselCard(
                            product = product,
                            onClick = { onNavigateToDetail(product) }
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            item {
                Text(
                    text = "Filtrar por Categoría",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("Todos") }
                        )
                    }
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) }
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            item {
                Text(
                    text = if (selectedCategory == null) "Todos los Productos" else "Productos: $selectedCategory",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(filteredProducts) { product ->
                ProductCard(
                    product = product,
                    isAnimating = addedProductId == product.id,
                    onAddToCart = {
                        addedProductId = product.id
                        cartViewModel.addToCart(product.id, currentUserId)
                        scope.launch {
                            delay(1000)
                            addedProductId = null
                        }
                    },
                    onClick = {
                        onNavigateToDetail(product)
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    isAnimating: Boolean,
    onAddToCart: () -> Unit,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    //animación de escala al presionar
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "scale"
    )
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(150)
            isPressed = false
        }
    }
    
    //animación cuando se agrega al carrito
    val addToCartScale by animateFloatAsState(
        targetValue = if (isAnimating) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "addToCartScale"
    )
    
    val addToCartAlpha by animateFloatAsState(
        targetValue = if (isAnimating) 0.5f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "addToCartAlpha"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(addToCartAlpha),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imagenUrl.isNotEmpty()) {
                    val painter = rememberAsyncImagePainter(
                        model = product.imagenUrl,
                        onError = { error ->
                            android.util.Log.e("ImageLoad", "Error cargando: ${product.imagenUrl}", error.result.throwable)
                        },
                        onSuccess = { success ->
                            android.util.Log.d("ImageLoad", "Imagen cargada: ${product.imagenUrl}")
                        }
                    )
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = product.titulo,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (painter.state is coil.compose.AsyncImagePainter.State.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center),
                                strokeWidth = 2.dp
                            )
                        }
                        if (painter.state is coil.compose.AsyncImagePainter.State.Error) {
                            Text(
                                text = "Vinilo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Vinilo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = product.artista,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${product.precio.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(
                onClick = {
                    isPressed = true
                    onAddToCart()
                }
            ) {
                Box(
                    modifier = Modifier.scale(addToCartScale),
                    contentAlignment = Alignment.Center
                ) {
                    if (isAnimating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Filled.AddShoppingCart,
                            contentDescription = "Agregar al carrito",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCarouselCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(280.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imagenUrl.isNotEmpty()) {
                    val painter = rememberAsyncImagePainter(model = product.imagenUrl)
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painter,
                            contentDescription = product.titulo,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (painter.state is coil.compose.AsyncImagePainter.State.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center),
                                strokeWidth = 2.dp
                            )
                        }
                        if (painter.state is coil.compose.AsyncImagePainter.State.Error) {
                            Text(
                                text = "Vinilo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Vinilo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = product.artista,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${product.precio.toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
