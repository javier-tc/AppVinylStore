package com.example.vinylstore.repository

import com.example.vinylstore.data.remote.api.OrderApi
import com.example.vinylstore.data.remote.dto.OrderResponse
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import okhttp3.ResponseBody as OkHttpResponseBody

class OrderRepositoryTest {
    private lateinit var orderApi: OrderApi
    private lateinit var repository: OrderRepository
    
    @Before
    fun setup() {
        orderApi = mockk()
        repository = OrderRepository(orderApi)
    }
    
    @Test
    fun `getMyOrders retorna lista de órdenes`() = runTest {
        val orders = listOf(
            OrderResponse(1, 1, 1, 2, 10.0, 20.0, "2024-01-15T10:00:00", "COMPLETADO")
        )
        val response = Response.success(orders)
        coEvery { orderApi.getMyOrders() } returns response
        
        val result = repository.getMyOrders()
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }
    
    @Test
    fun `createOrder crea orden con estado COMPLETADO por defecto`() = runTest {
        val orderResponse = OrderResponse(1, 1, 1, 2, 10.0, 20.0, "2024-01-15T10:00:00", "COMPLETADO")
        val response = Response.success(orderResponse)
        coEvery { orderApi.createOrder(any()) } returns response
        
        val result = repository.createOrder(1, 2, 10.0, "COMPLETADO")
        
        assertTrue(result.isSuccess)
        val orders = repository.orders.first()
        assertTrue(orders.isNotEmpty())
    }
    
    @Test
    fun `getAllOrders retorna todas las órdenes`() = runTest {
        val orders = listOf(
            OrderResponse(1, 1, 1, 2, 10.0, 20.0, "2024-01-15T10:00:00", "COMPLETADO"),
            OrderResponse(2, 2, 2, 1, 15.0, 15.0, "2024-01-15T11:00:00", "COMPLETADO")
        )
        val response = Response.success(orders)
        coEvery { orderApi.getAllOrders() } returns response
        
        val result = repository.getAllOrders()
        
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }
    
    @Test
    fun `getOrdersByEstado filtra por estado`() = runTest {
        val orders = listOf(
            OrderResponse(1, 1, 1, 2, 10.0, 20.0, "2024-01-15T10:00:00", "COMPLETADO")
        )
        val response = Response.success(orders)
        coEvery { orderApi.getOrdersByEstado(any()) } returns response
        
        val result = repository.getOrdersByEstado("COMPLETADO")
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }
    
    @Test
    fun `getMyOrders maneja timeout`() = runTest {
        coEvery { orderApi.getMyOrders() } throws java.net.SocketTimeoutException()
        
        val result = repository.getMyOrders()
        
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Timeout") == true)
    }
    
    @Test
    fun `getMyOrders maneja error de conexión`() = runTest {
        coEvery { orderApi.getMyOrders() } throws java.net.UnknownHostException()
        
        val result = repository.getMyOrders()
        
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("conexión") == true)
    }
    
    @Test
    fun `createOrder maneja error de respuesta`() = runTest {
        val response = Response.error<OrderResponse>(400, OkHttpResponseBody.create(null, ""))
        coEvery { orderApi.createOrder(any()) } returns response
        
        val result = repository.createOrder(1, 2, 10.0)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getAllOrders maneja error de respuesta`() = runTest {
        val response = Response.error<List<OrderResponse>>(500, OkHttpResponseBody.create(null, ""))
        coEvery { orderApi.getAllOrders() } returns response
        
        val result = repository.getAllOrders()
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getOrdersByEstado maneja error de respuesta`() = runTest {
        val response = Response.error<List<OrderResponse>>(400, OkHttpResponseBody.create(null, ""))
        coEvery { orderApi.getOrdersByEstado(any()) } returns response
        
        val result = repository.getOrdersByEstado("COMPLETADO")
        
        assertTrue(result.isFailure)
    }
}


