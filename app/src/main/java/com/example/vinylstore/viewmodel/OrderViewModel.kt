package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import com.example.vinylstore.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

//versión simplificada ya que la API no tiene endpoints de órdenes
class OrderViewModel : ViewModel() {
    
    fun getOrdersByUser(userId: Long): Flow<List<OrderWithProduct>> {
        //retornar lista vacía ya que la API no tiene endpoints de órdenes
        return flowOf(emptyList())
    }
    
    fun getAllOrders(): Flow<List<OrderWithProduct>> {
        return flowOf(emptyList())
    }
}

data class OrderWithProduct(
    val order: Order,
    val product: com.example.vinylstore.model.Product?
)
