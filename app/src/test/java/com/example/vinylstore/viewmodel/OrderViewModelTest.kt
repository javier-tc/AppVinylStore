package com.example.vinylstore.viewmodel

import com.example.vinylstore.data.model.Order
import com.example.vinylstore.repository.OrderRepository
import com.example.vinylstore.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class OrderViewModelTest {
    private lateinit var orderRepository: OrderRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var viewModel: OrderViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        orderRepository = mockk()
        productRepository = mockk()
        every { orderRepository.orders } returns MutableStateFlow(emptyList())
        coEvery { orderRepository.getMyOrders() } returns Result.success(emptyList())
        viewModel = OrderViewModel(orderRepository, productRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `getOrdersByUser filtra órdenes por usuario`() = runTest {
        val orders = listOf(
            Order(1, 1, 1, 2, 10.0, 20.0, System.currentTimeMillis(), "completado"),
            Order(2, 2, 2, 1, 15.0, 15.0, System.currentTimeMillis(), "completado")
        )
        every { orderRepository.orders } returns MutableStateFlow(orders)
        coEvery { productRepository.getProductById(any()) } returns Result.success(
            com.example.vinylstore.data.model.Product(1, "Producto", "Artista", 10.0, "", "", "", 10)
        )
        
        val result = viewModel.getOrdersByUser(1L).first()
        
        assertEquals(1, result.size)
        assertEquals(1L, result[0].order.userId)
    }
    
    @Test
    fun `getAllOrders retorna todas las órdenes cuando estado es null`() = runTest {
        val orders = listOf(
            Order(1, 1, 1, 2, 10.0, 20.0, System.currentTimeMillis(), "completado"),
            Order(2, 2, 2, 1, 15.0, 15.0, System.currentTimeMillis(), "pendiente")
        )
        every { orderRepository.orders } returns MutableStateFlow(orders)
        coEvery { productRepository.getProductById(any()) } returns Result.success(
            com.example.vinylstore.data.model.Product(1, "Producto", "Artista", 10.0, "", "", "", 10)
        )
        
        val result = viewModel.getAllOrders(null).first()
        
        assertEquals(2, result.size)
    }
    
    @Test
    fun `getAllOrders filtra por estado cuando se proporciona`() = runTest {
        val orders = listOf(
            Order(1, 1, 1, 2, 10.0, 20.0, System.currentTimeMillis(), "completado"),
            Order(2, 2, 2, 1, 15.0, 15.0, System.currentTimeMillis(), "pendiente")
        )
        every { orderRepository.orders } returns MutableStateFlow(orders)
        coEvery { productRepository.getProductById(any()) } returns Result.success(
            com.example.vinylstore.data.model.Product(1, "Producto", "Artista", 10.0, "", "", "", 10)
        )
        
        val result = viewModel.getAllOrders("completado").first()
        
        assertEquals(1, result.size)
        assertEquals("completado", result[0].order.estado)
    }
    
    @Test
    fun `refreshOrders llama a getMyOrders`() = runTest {
        coEvery { orderRepository.getMyOrders() } returns Result.success(emptyList())
        
        viewModel.refreshOrders()
        
        coVerify { orderRepository.getMyOrders() }
    }
    
    @Test
    fun `refreshAllOrders llama a getAllOrders`() = runTest {
        coEvery { orderRepository.getAllOrders() } returns Result.success(emptyList())
        
        viewModel.refreshAllOrders()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify(exactly = 1) { orderRepository.getAllOrders() }
    }
    
    @Test
    fun `refreshOrdersByEstado llama a getOrdersByEstado`() = runTest {
        coEvery { orderRepository.getOrdersByEstado(any()) } returns Result.success(emptyList())
        
        viewModel.refreshOrdersByEstado("COMPLETADO")
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify(exactly = 1) { orderRepository.getOrdersByEstado("COMPLETADO") }
    }
    
    @Test
    fun `getOrdersByUser retorna lista vacía cuando no hay órdenes`() = runTest {
        every { orderRepository.orders } returns MutableStateFlow(emptyList())
        
        val result = viewModel.getOrdersByUser(1L).first()
        
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `getAllOrders retorna lista vacía cuando no hay órdenes`() = runTest {
        every { orderRepository.orders } returns MutableStateFlow(emptyList())
        
        val result = viewModel.getAllOrders().first()
        
        assertTrue(result.isEmpty())
    }
    
    @Test
    fun `getOrdersByUser maneja producto no encontrado`() = runTest {
        val orders = listOf(
            Order(1, 1, 1, 2, 10.0, 20.0, System.currentTimeMillis(), "completado")
        )
        every { orderRepository.orders } returns MutableStateFlow(orders)
        coEvery { productRepository.getProductById(1) } returns Result.failure(Exception("No encontrado"))
        
        val result = viewModel.getOrdersByUser(1L).first()
        
        assertEquals(1, result.size)
        assertNull(result[0].product)
    }
}


