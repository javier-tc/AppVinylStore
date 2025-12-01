package com.example.vinylstore.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.vinylstore.data.remote.api.CartApi
import com.example.vinylstore.data.remote.api.OrderApi
import com.example.vinylstore.data.remote.api.ProductApi
import com.example.vinylstore.repository.CartRepository
import com.example.vinylstore.repository.OrderRepository
import com.example.vinylstore.repository.ProductRepository
import com.example.vinylstore.ui.theme.VinylStoreTheme
import com.example.vinylstore.viewmodel.CartViewModel
import org.junit.Rule
import org.junit.Test

class CartScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private fun createFakeCartRepository(): CartRepository {
        val fakeApi = object : CartApi {
            override suspend fun getCart(userId: Int) = retrofit2.Response.success<List<com.example.vinylstore.data.remote.dto.CartItemDto>>(emptyList())
            override suspend fun addItem(userId: Int, request: com.example.vinylstore.data.remote.dto.AddCartItemRequest) = 
                retrofit2.Response.success(com.example.vinylstore.data.remote.dto.CartItemDto(1, userId, request.productId, request.quantity, 0.0, 0.0))
            override suspend fun updateItem(userId: Int, itemId: Int, request: com.example.vinylstore.data.remote.dto.UpdateCartItemRequest) = 
                retrofit2.Response.success(com.example.vinylstore.data.remote.dto.CartItemDto(itemId, userId, 1, request.quantity, 0.0, 0.0))
            override suspend fun deleteItem(userId: Int, itemId: Int) = retrofit2.Response.success(Unit)
            override suspend fun clearCart(userId: Int) = retrofit2.Response.success(Unit)
            override suspend fun getCartTotal(userId: Int) = retrofit2.Response.success(com.example.vinylstore.data.remote.dto.CartTotalResponse(userId, emptyList(), 0.0, 0))
        }
        return CartRepository(fakeApi)
    }
    
    private fun createFakeProductRepository(): ProductRepository {
        val fakeApi = object : ProductApi {
            override suspend fun getProducts(genero: String?) = retrofit2.Response.success<List<com.example.vinylstore.data.remote.dto.ProductDto>>(emptyList())
            override suspend fun getProductById(id: Int) = 
                retrofit2.Response.success(com.example.vinylstore.data.remote.dto.ProductDto(id, "Test", "Artist", "Rock", "Desc", 10.0, 5, ""))
            override suspend fun createProduct(request: com.example.vinylstore.data.remote.dto.CreateProductRequest) = 
                retrofit2.Response.success(com.example.vinylstore.data.remote.dto.ProductDto(1, request.title, request.artist, request.genre, request.description, request.price, request.stock, request.imageUrl))
            override suspend fun updateProduct(id: Int, request: com.example.vinylstore.data.remote.dto.CreateProductRequest) = 
                retrofit2.Response.success(com.example.vinylstore.data.remote.dto.ProductDto(id, request.title, request.artist, request.genre, request.description, request.price, request.stock, request.imageUrl))
            override suspend fun deleteProduct(id: Int) = retrofit2.Response.success(Unit)
            override suspend fun updateStock(id: Int, request: com.example.vinylstore.data.remote.dto.UpdateStockRequest) = 
                retrofit2.Response.success(com.example.vinylstore.data.remote.dto.ProductDto(id, "Test", "Artist", "Rock", "Desc", 10.0, request.stock, ""))
        }
        return ProductRepository(fakeApi)
    }
    
    private fun createFakeOrderRepository(): OrderRepository {
        val fakeApi = object : com.example.vinylstore.data.remote.api.OrderApi {
            override suspend fun getMyOrders() = retrofit2.Response.success<List<com.example.vinylstore.data.remote.dto.OrderResponse>>(emptyList())
            override suspend fun getAllOrders() = retrofit2.Response.success<List<com.example.vinylstore.data.remote.dto.OrderResponse>>(emptyList())
            override suspend fun getOrdersByEstado(estado: String) = retrofit2.Response.success<List<com.example.vinylstore.data.remote.dto.OrderResponse>>(emptyList())
            override suspend fun createOrder(request: com.example.vinylstore.data.remote.dto.OrderRequest) = 
                retrofit2.Response.success(com.example.vinylstore.data.remote.dto.OrderResponse(1L, request.productId, 1L, request.cantidad, request.precioUnitario, request.cantidad * request.precioUnitario, "2024-01-01T00:00:00", request.estado ?: "COMPLETADO"))
        }
        return OrderRepository(fakeApi)
    }
    
    @Test
    fun cartScreen_muestraTitulo() {
        val cartRepository = createFakeCartRepository()
        val productRepository = createFakeProductRepository()
        val orderRepository = createFakeOrderRepository()
        val cartViewModel = CartViewModel(cartRepository, productRepository, orderRepository)
        
        composeTestRule.setContent {
            VinylStoreTheme {
                CartScreen(
                    cartViewModel = cartViewModel,
                    currentUserId = 1L,
                    onBack = {},
                    onConfirmOrder = {}
                )
            }
        }
        
        composeTestRule.waitForIdle()
    }
    
    @Test
    fun cartScreen_muestraCarritoVacio() {
        val cartRepository = createFakeCartRepository()
        val productRepository = createFakeProductRepository()
        val orderRepository = createFakeOrderRepository()
        val cartViewModel = CartViewModel(cartRepository, productRepository, orderRepository)
        
        composeTestRule.setContent {
            VinylStoreTheme {
                CartScreen(
                    cartViewModel = cartViewModel,
                    currentUserId = 1L,
                    onBack = {},
                    onConfirmOrder = {}
                )
            }
        }
        
        composeTestRule.waitForIdle()
    }
    
    @Test
    fun cartScreen_muestraBotonVolver() {
        val cartRepository = createFakeCartRepository()
        val productRepository = createFakeProductRepository()
        val orderRepository = createFakeOrderRepository()
        val cartViewModel = CartViewModel(cartRepository, productRepository, orderRepository)
        var onBackCalled = false
        
        composeTestRule.setContent {
            VinylStoreTheme {
                CartScreen(
                    cartViewModel = cartViewModel,
                    currentUserId = 1L,
                    onBack = { onBackCalled = true },
                    onConfirmOrder = {}
                )
            }
        }
        
        composeTestRule.waitForIdle()
    }
}

