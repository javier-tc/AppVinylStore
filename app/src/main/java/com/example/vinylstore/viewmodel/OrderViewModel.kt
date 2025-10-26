package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.OrderDao
import com.example.vinylstore.data.ProductDao
import com.example.vinylstore.model.CartItem
import com.example.vinylstore.model.Order
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class OrderViewModel(
    private val orderDao: OrderDao,
    private val productDao: ProductDao
) : ViewModel() {
    
    fun getOrdersByUser(userId: Long): Flow<List<OrderWithProduct>> {
        return orderDao.getOrdersByUser(userId).flatMapLatest { orders ->
            flow {
                val result = orders.map { order ->
                    val product = productDao.getProductById(order.productId)
                    OrderWithProduct(order, product)
                }
                emit(result)
            }
        }
    }
    
    fun getAllOrders(): Flow<List<OrderWithProduct>> {
        return orderDao.getAllOrders().flatMapLatest { orders ->
            flow {
                val result = orders.map { order ->
                    val product = productDao.getProductById(order.productId)
                    OrderWithProduct(order, product)
                }
                emit(result)
            }
        }
    }
    
    fun confirmOrder(cartItems: List<CartItem>, userId: Long) {
        viewModelScope.launch {
            cartItems.forEach { cartItem ->
                val product = productDao.getProductById(cartItem.productId)
                if (product != null) {
                    val order = Order(
                        productId = cartItem.productId,
                        userId = userId,
                        cantidad = cartItem.cantidad,
                        precioUnitario = product.precio,
                        total = product.precio * cartItem.cantidad,
                        estado = "confirmado"
                    )
                    orderDao.insertOrder(order)
                    
                    //reducir stock
                    val nuevoStock = product.stock - cartItem.cantidad
                    if (nuevoStock >= 0) {
                        productDao.updateStock(cartItem.productId, nuevoStock)
                    }
                }
            }
        }
    }
    
    fun updateOrderStatus(orderId: Long, estado: String) {
        viewModelScope.launch {
            orderDao.updateOrderStatus(orderId, estado)
        }
    }
    
    sealed class OrderState {
        object Success : OrderState()
        data class Error(val message: String) : OrderState()
    }
}

data class OrderWithProduct(
    val order: Order,
    val product: com.example.vinylstore.model.Product?
)
