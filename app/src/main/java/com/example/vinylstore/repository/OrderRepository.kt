package com.example.vinylstore.repository

import com.example.vinylstore.data.model.Order
import com.example.vinylstore.data.remote.api.OrderApi
import com.example.vinylstore.data.remote.dto.OrderRequest
import com.example.vinylstore.data.remote.dto.OrderResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderRepository(
    private val orderApi: OrderApi
) {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: Flow<List<Order>> = _orders.asStateFlow()
    
    suspend fun getMyOrders(): Result<List<Order>> {
        return try {
            val response = orderApi.getMyOrders()
            if (response.isSuccessful && response.body() != null) {
                val orders = response.body()!!.map { it.toOrder() }
                _orders.value = orders
                Result.success(orders)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                Result.failure(Exception("Error al obtener pedidos: ${response.code()} - ${response.message()}. Detalles: $errorBody"))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Result.failure(Exception("Timeout: El servidor no respondió a tiempo."))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("Error de conexión: No se pudo conectar al servidor."))
        } catch (e: Exception) {
            Result.failure(Exception("Error al obtener pedidos: ${e.message ?: e.javaClass.simpleName}"))
        }
    }
    
    suspend fun getAllOrders(): Result<List<Order>> {
        return try {
            val response = orderApi.getAllOrders()
            if (response.isSuccessful && response.body() != null) {
                val orders = response.body()!!.map { it.toOrder() }
                Result.success(orders)
            } else {
                Result.failure(Exception("Error al obtener pedidos: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getOrdersByEstado(estado: String): Result<List<Order>> {
        return try {
            val response = orderApi.getOrdersByEstado(estado)
            if (response.isSuccessful && response.body() != null) {
                val orders = response.body()!!.map { it.toOrder() }
                Result.success(orders)
            } else {
                Result.failure(Exception("Error al obtener pedidos: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createOrder(productId: Int, cantidad: Int, precioUnitario: Double, estado: String = "PENDIENTE"): Result<Order> {
        return try {
            val request = OrderRequest(productId, cantidad, precioUnitario, estado)
            val response = orderApi.createOrder(request)
            if (response.isSuccessful && response.body() != null) {
                val order = response.body()!!.toOrder()
                _orders.value = _orders.value + order
                Result.success(order)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Sin detalles"
                Result.failure(Exception("Error al crear pedido: ${response.code()} - ${response.message()}. Detalles: $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al crear pedido: ${e.message ?: e.javaClass.simpleName}"))
        }
    }
    
}

