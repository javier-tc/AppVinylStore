package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.model.Order
import com.example.vinylstore.data.model.Product
import com.example.vinylstore.repository.OrderRepository
import com.example.vinylstore.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) : ViewModel() {
    
    init {
        viewModelScope.launch {
            orderRepository.getMyOrders()
        }
    }
    
    fun getOrdersByUser(userId: Long): Flow<List<OrderWithProduct>> {
        return orderRepository.orders.flatMapLatest { orders ->
            val userOrders = orders.filter { it.userId == userId }
            if (userOrders.isEmpty()) {
                flowOf(emptyList())
            } else {
                flow {
                    val result = mutableListOf<OrderWithProduct>()
                    userOrders.forEach { order ->
                        //intentar obtener el producto desde el repository primero
                        val productResult = productRepository.getProductById(order.productId)
                        val product = productResult.getOrNull()
                        result.add(OrderWithProduct(order, product))
                    }
                    emit(result)
                }
            }
        }
    }
    
    fun getAllOrders(estado: String? = null): Flow<List<OrderWithProduct>> {
        return orderRepository.orders.flatMapLatest { orders ->
            val filteredOrders = if (estado != null) {
                orders.filter { it.estado == estado }
            } else {
                orders
            }
            if (filteredOrders.isEmpty()) {
                flowOf(emptyList())
            } else {
                flow {
                    val result = mutableListOf<OrderWithProduct>()
                    filteredOrders.forEach { order ->
                        val productResult = productRepository.getProductById(order.productId)
                        val product = productResult.getOrNull()
                        result.add(OrderWithProduct(order, product))
                    }
                    emit(result)
                }
            }
        }
    }
    
    fun refreshOrders() {
        viewModelScope.launch {
            orderRepository.getMyOrders()
        }
    }
    
    fun refreshAllOrders() {
        viewModelScope.launch {
            orderRepository.getAllOrders()
        }
    }
    
    fun refreshOrdersByEstado(estado: String) {
        viewModelScope.launch {
            orderRepository.getOrdersByEstado(estado)
        }
    }
}

data class OrderWithProduct(
    val order: Order,
    val product: Product?
)
