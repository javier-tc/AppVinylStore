package com.example.vinylstore.viewmodel

import com.example.vinylstore.data.model.CartItem
import com.example.vinylstore.data.model.Product
import com.example.vinylstore.repository.CartRepository
import com.example.vinylstore.repository.OrderRepository
import com.example.vinylstore.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CartViewModelTest {
    private lateinit var cartRepository: CartRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var viewModel: CartViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        cartRepository = mockk()
        productRepository = mockk()
        orderRepository = mockk()
        viewModel = CartViewModel(cartRepository, productRepository, orderRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `addToCart agrega un nuevo item al carrito`() = runTest {
        val cartItems = MutableStateFlow<List<CartItem>>(emptyList())
        every { cartRepository.cartItems } returns cartItems
        coEvery { cartRepository.addItem(any(), any(), any()) } returns Result.success(
            CartItem(1, 1, 1, 1)
        )
        
        viewModel.addToCart(1, 1L, 1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { cartRepository.addItem(1, 1, 1) }
    }
    
    @Test
    fun `addToCart actualiza cantidad si el item ya existe`() = runTest {
        val existingItem = CartItem(1, 1, 2, 1)
        val cartItems = MutableStateFlow(listOf(existingItem))
        every { cartRepository.cartItems } returns cartItems
        coEvery { cartRepository.updateItem(any(), any(), any()) } returns Result.success(
            CartItem(1, 1, 3, 1)
        )
        
        viewModel.addToCart(1, 1L, 1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { cartRepository.updateItem(1, 1, 3) }
    }
    
    @Test
    fun `removeFromCart elimina un item del carrito`() = runTest {
        coEvery { cartRepository.deleteItem(any(), any()) } returns Result.success(Unit)
        
        val cartItem = CartItem(1, 1, 1, 1)
        viewModel.removeFromCart(cartItem)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { cartRepository.deleteItem(1, 1) }
    }
    
    @Test
    fun `updateCartItemQuantity actualiza la cantidad`() = runTest {
        coEvery { cartRepository.updateItem(any(), any(), any()) } returns Result.success(
            CartItem(1, 1, 5, 1)
        )
        
        val cartItem = CartItem(1, 1, 3, 1)
        viewModel.updateCartItemQuantity(cartItem, 5)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { cartRepository.updateItem(1, 1, 5) }
    }
    
    @Test
    fun `updateCartItemQuantity elimina item si cantidad es 0`() = runTest {
        coEvery { cartRepository.deleteItem(any(), any()) } returns Result.success(Unit)
        
        val cartItem = CartItem(1, 1, 1, 1)
        viewModel.updateCartItemQuantity(cartItem, 0)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { cartRepository.deleteItem(1, 1) }
    }
    
    @Test
    fun `confirmOrder crea órdenes para todos los items`() = runTest {
        val cartItems = listOf(
            CartItem(1, 1, 2, 1),
            CartItem(2, 2, 1, 1)
        )
        val product1 = Product(1, "Producto 1", "Artista 1", 10.0, "", "", "", 10)
        val product2 = Product(2, "Producto 2", "Artista 2", 20.0, "", "", "", 5)
        
        coEvery { productRepository.getProductById(1) } returns Result.success(product1)
        coEvery { productRepository.getProductById(2) } returns Result.success(product2)
        coEvery { orderRepository.createOrder(any(), any(), any()) } returns Result.success(
            com.example.vinylstore.data.model.Order(1, 1, 1, 2, 10.0, 20.0, System.currentTimeMillis(), "completado")
        )
        coEvery { cartRepository.clearCart(any()) } returns Result.success(Unit)
        
        val result = viewModel.confirmOrder(cartItems, 1L)
        
        assertTrue(result.isSuccess)
        coVerify(exactly = 2) { orderRepository.createOrder(any(), any(), any()) }
        coVerify { cartRepository.clearCart(1) }
    }
    
    @Test
    fun `getTotalPrice retorna el total correcto`() = runTest {
        coEvery { cartRepository.getCartTotal(any()) } returns Result.success(
            com.example.vinylstore.data.remote.dto.CartTotalResponse(1, emptyList(), 50.0, 0)
        )
        
        val total = viewModel.getTotalPrice(1L)
        
        assertEquals(50.0, total, 0.01)
    }
    
    @Test
    fun `getTotalPrice retorna 0 cuando hay error`() = runTest {
        coEvery { cartRepository.getCartTotal(any()) } returns Result.failure(Exception("Error"))
        
        val total = viewModel.getTotalPrice(1L)
        
        assertEquals(0.0, total, 0.01)
    }
    
    @Test
    fun `getCartItems retorna items con productos`() = runTest {
        val cartItems = listOf(
            CartItem(1, 1, 1, 1)
        )
        val product = Product(1, "Producto", "Artista", 10.0, "", "", "", 10)
        every { cartRepository.cartItems } returns MutableStateFlow(cartItems)
        coEvery { productRepository.getProductById(1) } returns Result.success(product)
        
        val result = viewModel.getCartItems(1L).first()
        
        assertEquals(1, result.size)
        assertEquals(product, result[0].product)
    }
    
    @Test
    fun `getCartItems retorna lista vacía cuando no hay items`() = runTest {
        every { cartRepository.cartItems } returns MutableStateFlow(emptyList())
        
        val result = viewModel.getCartItems(1L).first()
        
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `getCartItemCount retorna cantidad correcta`() = runTest {
        every { cartRepository.getCartItemCount(any()) } returns flowOf(3)
        
        val count = viewModel.getCartItemCount(1L).first()
        
        assertEquals(3, count)
    }
    
    @Test
    fun `loadCart carga el carrito`() = runTest {
        coEvery { cartRepository.getCart(any()) } returns Result.success(emptyList())
        
        viewModel.loadCart(1L)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { cartRepository.getCart(1) }
    }
    
    @Test
    fun `clearCart limpia el carrito`() = runTest {
        coEvery { cartRepository.clearCart(any()) } returns Result.success(Unit)
        
        viewModel.clearCart(1L)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { cartRepository.clearCart(1) }
    }
    
    @Test
    fun `confirmOrder maneja error al obtener producto`() = runTest {
        val cartItems = listOf(CartItem(1, 1, 1, 1))
        coEvery { productRepository.getProductById(1) } returns Result.failure(Exception("Error"))
        
        val result = viewModel.confirmOrder(cartItems, 1L)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `confirmOrder maneja error al crear orden`() = runTest {
        val cartItems = listOf(CartItem(1, 1, 1, 1))
        val product = Product(1, "Producto", "Artista", 10.0, "", "", "", 10)
        coEvery { productRepository.getProductById(1) } returns Result.success(product)
        coEvery { orderRepository.createOrder(any(), any(), any()) } returns Result.failure(Exception("Error"))
        
        val result = viewModel.confirmOrder(cartItems, 1L)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `confirmOrder maneja error al limpiar carrito`() = runTest {
        val cartItems = listOf(CartItem(1, 1, 1, 1))
        val product = Product(1, "Producto", "Artista", 10.0, "", "", "", 10)
        coEvery { productRepository.getProductById(1) } returns Result.success(product)
        coEvery { orderRepository.createOrder(any(), any(), any()) } returns Result.success(
            com.example.vinylstore.data.model.Order(1, 1, 1, 1, 10.0, 10.0, System.currentTimeMillis(), "completado")
        )
        coEvery { cartRepository.clearCart(any()) } returns Result.failure(Exception("Error"))
        
        val result = viewModel.confirmOrder(cartItems, 1L)
        
        assertTrue(result.isFailure)
    }
}

