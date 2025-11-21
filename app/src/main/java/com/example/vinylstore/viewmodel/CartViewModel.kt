package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.model.CartItem
import com.example.vinylstore.data.model.Product
import com.example.vinylstore.repository.CartRepository
import com.example.vinylstore.repository.OrderRepository
import com.example.vinylstore.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class CartViewModel(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    fun getCartItems(userId: Long): Flow<List<CartItemWithProduct>> {
        return cartRepository.cartItems.flatMapLatest { cartItems ->
            val filteredItems = cartItems.filter { it.userId == userId }
            if (filteredItems.isEmpty()) {
                flowOf(emptyList())
            } else {
                flow {
                    val result = mutableListOf<CartItemWithProduct>()
                    filteredItems.forEach { cartItem ->
                        val productResult = productRepository.getProductById(cartItem.productId)
                        val product = productResult.getOrNull() ?: 
                            Product(0, "", "", 0.0, "", "", "")
                        result.add(CartItemWithProduct(cartItem, product))
                    }
                    emit(result)
                }
            }
        }
    }
    
    fun getCartItemCount(userId: Long): Flow<Int> {
        return cartRepository.getCartItemCount(userId.toInt())
    }
    
    fun loadCart(userId: Long) {
        viewModelScope.launch {
            cartRepository.getCart(userId.toInt())
        }
    }
    
    fun addToCart(productId: Int, userId: Long, quantity: Int = 1) {
        viewModelScope.launch {
            val existingItems = cartRepository.cartItems.first()
            val existingItem = existingItems.find { 
                it.productId == productId && it.userId == userId 
            }
            
            if (existingItem != null) {
                val newQuantity = existingItem.cantidad + quantity
                cartRepository.updateItem(
                    userId.toInt(),
                    existingItem.id.toInt(),
                    newQuantity
                )
            } else {
                cartRepository.addItem(userId.toInt(), productId, quantity)
            }
        }
    }
    
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartRepository.deleteItem(cartItem.userId.toInt(), cartItem.id.toInt())
        }
    }
    
    fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                cartRepository.updateItem(
                    cartItem.userId.toInt(),
                    cartItem.id.toInt(),
                    newQuantity
                )
            } else {
                cartRepository.deleteItem(cartItem.userId.toInt(), cartItem.id.toInt())
            }
        }
    }
    
    fun clearCart(userId: Long) {
        viewModelScope.launch {
            cartRepository.clearCart(userId.toInt())
        }
    }
    
    suspend fun getTotalPrice(userId: Long): Double {
        val result = cartRepository.getCartTotal(userId.toInt())
        return result.getOrNull()?.total ?: 0.0
    }
    
    suspend fun confirmOrder(cartItems: List<CartItem>, userId: Long): Result<Unit> {
        return try {
            //crear una orden por cada item del carrito
            cartItems.forEach { cartItem ->
                val productResult = productRepository.getProductById(cartItem.productId)
                val product = productResult.getOrNull()
                
                if (product != null) {
                    val orderResult = orderRepository.createOrder(
                        productId = cartItem.productId,
                        cantidad = cartItem.cantidad,
                        precioUnitario = product.precio
                    )
                    
                    if (orderResult.isFailure) {
                        return Result.failure(
                            Exception("Error al crear pedido para ${product.titulo}: ${orderResult.exceptionOrNull()?.message}")
                        )
                    }
                }
            }
            
            //limpiar el carrito solo si todas las órdenes se crearon exitosamente
            val clearResult = cartRepository.clearCart(userId.toInt())
            if (clearResult.isSuccess) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Pedidos creados pero error al limpiar carrito"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al confirmar pedido: ${e.message}"))
        }
    }
}

data class CartItemWithProduct(
    val cartItem: CartItem,
    val product: Product
)
