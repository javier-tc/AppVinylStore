package com.example.vinylstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vinylstore.data.model.Product
import com.example.vinylstore.data.model.User
import com.example.vinylstore.data.remote.RetrofitClient
import com.example.vinylstore.data.remote.SessionManager
import com.example.vinylstore.repository.AuthRepository
import com.example.vinylstore.repository.CartRepository
import com.example.vinylstore.repository.MusicRepository
import com.example.vinylstore.repository.OrderRepository
import com.example.vinylstore.repository.ProductRepository
import com.example.vinylstore.ui.screens.*
import com.example.vinylstore.ui.theme.VinylStoreTheme
import com.example.vinylstore.viewmodel.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val sessionManager = SessionManager(this)
        val authRepository = AuthRepository(
            RetrofitClient.createAuthApi(sessionManager),
            sessionManager
        )
        val productRepository = ProductRepository(
            RetrofitClient.createProductApi(sessionManager)
        )
        val cartRepository = CartRepository(
            RetrofitClient.createCartApi(sessionManager)
        )
        val orderRepository = OrderRepository(
            RetrofitClient.createOrderApi(sessionManager)
        )
        
        // API Key de Last.fm - VinylStore
        val lastFmApiKey = "e57b91397ed0950bb0e4ef8e33c367b3"
        val musicRepository = MusicRepository(
            RetrofitClient.createMusicApi(),
            lastFmApiKey
        )
        
        val viewModelFactory = ViewModelFactory(
            authRepository,
            productRepository,
            cartRepository,
            orderRepository,
            musicRepository
        )
        
        setContent {
            VinylStoreTheme {
                VinylStoreApp(viewModelFactory = viewModelFactory)
            }
        }
    }
}

@Composable
fun VinylStoreApp(viewModelFactory: ViewModelFactory) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
    val productViewModel: ProductViewModel = viewModel(factory = viewModelFactory) 
    val cartViewModel: CartViewModel = viewModel(factory = viewModelFactory)
    
    var currentUser by remember { mutableStateOf<User?>(null) }
    var previousUser by remember { mutableStateOf<User?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    
    LaunchedEffect(Unit) {
        authViewModel.currentUser.collect { user ->
            previousUser = currentUser
            currentUser = user
        }
    }
    
    //navegar a login cuando el usuario cierra sesión (cambia de no-null a null)
    LaunchedEffect(currentUser) {
        if (previousUser != null && currentUser == null && navController.currentDestination?.route != "login") {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    
    val startDestination = if (currentUser != null) "products" else "login"
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("products") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("products") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("products") {
            ProductsScreen(
                viewModel = productViewModel,
                cartViewModel = cartViewModel,
                currentUserId = currentUser?.id ?: 0,
                onNavigateToCart = {
                    navController.navigate("cart")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToDetail = { product ->
                    selectedProduct = product
                    navController.navigate("product_detail")
                }
            )
        }
        
        composable("product_detail") {
            selectedProduct?.let { product ->
                ProductDetailScreen(
                    product = product,
                    cartViewModel = cartViewModel,
                    currentUserId = currentUser?.id ?: 0,
                    onBack = {
                        selectedProduct = null
                        navController.popBackStack()
                    },
                    onAddToCart = {
                        navController.navigate("cart")
                    }
                )
            }
        }
        
        composable("cart") {
            CartScreen(
                cartViewModel = cartViewModel,
                currentUserId = currentUser?.id ?: 0,
                onBack = {
                    navController.popBackStack()
                },
                onConfirmOrder = {
                    navController.popBackStack()
                    navController.navigate("order_history")
                }
            )
        }
        
        composable("profile") {
            val musicViewModel: MusicViewModel = viewModel(factory = viewModelFactory)
            ProfileScreen(
                userName = currentUser?.nombre ?: "",
                userEmail = currentUser?.email ?: "",
                isAdmin = currentUser?.rol == "administrador",
                musicViewModel = musicViewModel,
                onLogout = {
                    authViewModel.logout()
                    //la navegación se maneja automáticamente cuando currentUser se vuelve null
                },
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToOrderHistory = {
                    navController.navigate("order_history")
                },
                onNavigateToAdminProducts = {
                    if (currentUser?.rol == "administrador") {
                        navController.navigate("admin_products")
                    }
                }
            )
        }
        
        composable("order_history") {
            val orderViewModel: OrderViewModel = viewModel(factory = viewModelFactory)
            OrderHistoryScreen(
                orderViewModel = orderViewModel,
                userId = currentUser?.id ?: 0,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("admin_products") {
            AdminProductsScreen(
                viewModel = productViewModel,
                onBack = {
                    navController.popBackStack()
                },
                onAddProduct = {
                    editingProduct = null
                    navController.navigate("product_form")
                },
                onEditProduct = { product ->
                    editingProduct = product
                    navController.navigate("product_form")
                }
            )
        }
        
        composable("product_form") {
            ProductFormScreen(
                viewModel = productViewModel,
                product = editingProduct,
                onSave = {
                    editingProduct = null
                    navController.popBackStack()
                },
                onCancel = {
                    editingProduct = null
                    navController.popBackStack()
                }
            )
        }
    }
}