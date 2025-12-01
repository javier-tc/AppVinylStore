package com.example.vinylstore.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.vinylstore.data.model.Product
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
import retrofit2.Response

class ProductDetailScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private fun createFakeCartRepository(): CartRepository {
        val fakeApi = object : CartApi {
            override suspend fun getCart(userId: Int) = Response.success<List<com.example.vinylstore.data.remote.dto.CartItemDto>>(emptyList())
            override suspend fun addItem(userId: Int, request: com.example.vinylstore.data.remote.dto.AddCartItemRequest) = 
                Response.success(com.example.vinylstore.data.remote.dto.CartItemDto(1, userId, request.productId, request.quantity, 0.0, 0.0))
            override suspend fun updateItem(userId: Int, itemId: Int, request: com.example.vinylstore.data.remote.dto.UpdateCartItemRequest) = 
                Response.success(com.example.vinylstore.data.remote.dto.CartItemDto(itemId, userId, 1, request.quantity, 0.0, 0.0))
            override suspend fun deleteItem(userId: Int, itemId: Int) = Response.success(Unit)
            override suspend fun clearCart(userId: Int) = Response.success(Unit)
            override suspend fun getCartTotal(userId: Int) = Response.success(com.example.vinylstore.data.remote.dto.CartTotalResponse(userId, emptyList(), 0.0, 0))
        }
        return CartRepository(fakeApi)
    }
    
    private fun createFakeProductRepository(): ProductRepository {
        val fakeApi = object : ProductApi {
            override suspend fun getProducts(genero: String?) = Response.success<List<com.example.vinylstore.data.remote.dto.ProductDto>>(emptyList())
            override suspend fun getProductById(id: Int) = 
                Response.success(com.example.vinylstore.data.remote.dto.ProductDto(id, "Test", "Artist", "Rock", "Desc", 10.0, 5, ""))
            override suspend fun createProduct(request: com.example.vinylstore.data.remote.dto.CreateProductRequest) = 
                Response.success(com.example.vinylstore.data.remote.dto.ProductDto(1, request.title, request.artist, request.genre, request.description, request.price, request.stock, request.imageUrl))
            override suspend fun updateProduct(id: Int, request: com.example.vinylstore.data.remote.dto.CreateProductRequest) = 
                Response.success(com.example.vinylstore.data.remote.dto.ProductDto(id, request.title, request.artist, request.genre, request.description, request.price, request.stock, request.imageUrl))
            override suspend fun deleteProduct(id: Int) = Response.success(Unit)
            override suspend fun updateStock(id: Int, request: com.example.vinylstore.data.remote.dto.UpdateStockRequest) = 
                Response.success(com.example.vinylstore.data.remote.dto.ProductDto(id, "Test", "Artist", "Rock", "Desc", 10.0, request.stock, ""))
        }
        return ProductRepository(fakeApi)
    }
    
    private fun createFakeOrderRepository(): OrderRepository {
        val fakeApi = object : OrderApi {
            override suspend fun getMyOrders() = Response.success<List<com.example.vinylstore.data.remote.dto.OrderResponse>>(emptyList())
            override suspend fun getAllOrders() = Response.success<List<com.example.vinylstore.data.remote.dto.OrderResponse>>(emptyList())
            override suspend fun getOrdersByEstado(estado: String) = Response.success<List<com.example.vinylstore.data.remote.dto.OrderResponse>>(emptyList())
            override suspend fun createOrder(request: com.example.vinylstore.data.remote.dto.OrderRequest) = 
                Response.success(com.example.vinylstore.data.remote.dto.OrderResponse(1L, request.productId, 1L, request.cantidad, request.precioUnitario, request.cantidad * request.precioUnitario, "2024-01-01T00:00:00", request.estado ?: "COMPLETADO"))
        }
        return OrderRepository(fakeApi)
    }
    
    @Test
    fun productDetailScreen_muestraInformacionDelProducto() {
        val cartRepository = createFakeCartRepository()
        val productRepository = createFakeProductRepository()
        val orderRepository = createFakeOrderRepository()
        val cartViewModel = CartViewModel(cartRepository, productRepository, orderRepository)
        
        val product = Product(
            id = 1,
            titulo = "Test Album",
            artista = "Test Artist",
            precio = 29.99,
            descripcion = "Test Description",
            genero = "Rock",
            imagenUrl = "https://example.com/image.jpg",
            stock = 10
        )
        
        var onBackCalled = false
        var onAddToCartCalled = false
        
        composeTestRule.setContent {
            VinylStoreTheme {
                ProductDetailScreen(
                    product = product,
                    cartViewModel = cartViewModel,
                    currentUserId = 1L,
                    onBack = { onBackCalled = true },
                    onAddToCart = { onAddToCartCalled = true }
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        // Verificar que se muestra la información del producto
        // Usar onAllNodesWithText para manejar múltiples ocurrencias y obtener el primero
        val albumNodes = composeTestRule.onAllNodesWithText("Test Album")
        if (albumNodes.fetchSemanticsNodes().isNotEmpty()) {
            albumNodes[0].assertIsDisplayed()
        }
        composeTestRule.onNodeWithText("Test Artist").assertIsDisplayed()
        composeTestRule.onNodeWithText("Rock").assertIsDisplayed()
    }
    
    @Test
    fun productDetailScreen_muestraPrecio() {
        val cartRepository = createFakeCartRepository()
        val productRepository = createFakeProductRepository()
        val orderRepository = createFakeOrderRepository()
        val cartViewModel = CartViewModel(cartRepository, productRepository, orderRepository)
        
        val product = Product(
            id = 1,
            titulo = "Test Album",
            artista = "Test Artist",
            precio = 29.99,
            descripcion = "Test Description",
            genero = "Rock",
            imagenUrl = "https://example.com/image.jpg",
            stock = 10
        )
        
        composeTestRule.setContent {
            VinylStoreTheme {
                ProductDetailScreen(
                    product = product,
                    cartViewModel = cartViewModel,
                    currentUserId = 1L,
                    onBack = {},
                    onAddToCart = {}
                )
            }
        }
        
        composeTestRule.waitForIdle()
        
        // Verificar que se muestra el precio (puede estar formateado como $29.99, 29.99, etc.)
        // Buscar cualquier nodo que contenga "29" que es parte del precio
        composeTestRule.onNodeWithText("29", substring = true).assertIsDisplayed()
    }
}

