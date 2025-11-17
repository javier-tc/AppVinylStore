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
import com.example.vinylstore.data.VinylStoreDatabase
import com.example.vinylstore.model.Product
import com.example.vinylstore.model.User
import com.example.vinylstore.ui.screens.*
import com.example.vinylstore.ui.theme.VinylStoreTheme
import com.example.vinylstore.viewmodel.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = VinylStoreDatabase.getDatabase(this)
        val viewModelFactory = ViewModelFactory(
            database.userDao(),
            database.productDao(),
            database.cartDao(),
            database.orderDao()
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
    val orderViewModel: OrderViewModel = viewModel(factory = viewModelFactory)
    
    var currentUser by remember { mutableStateOf<User?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    
    LaunchedEffect(Unit) {
        authViewModel.currentUser.collect { user ->
            currentUser = user
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
            ProfileScreen(
                userName = currentUser?.nombre ?: "",
                userEmail = currentUser?.email ?: "",
                isAdmin = currentUser?.rol == "administrador",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("products") {
                        popUpTo(0) { inclusive = true }
                    }
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