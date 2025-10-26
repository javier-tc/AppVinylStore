package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.CartDao
import com.example.vinylstore.data.OrderDao
import com.example.vinylstore.data.ProductDao
import com.example.vinylstore.model.CartItem
import com.example.vinylstore.model.Order
import com.example.vinylstore.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

class CartViewModel(
    private val cartDao: CartDao,
    private val productDao: ProductDao,
    private val orderDao: OrderDao
) : ViewModel() {
    
    fun getCartItems(userId: Long): Flow<List<CartItemWithProduct>> {
        return cartDao.getCartItems(userId).map { cartItems ->
            cartItems.map { cartItem ->
                val product = productDao.getProductById(cartItem.productId) ?: 
                    Product(0, "", "", 0.0, "", "", "")
                CartItemWithProduct(cartItem, product)
            }
        }
    }
    
    fun addToCart(productId: Int, userId: Long, quantity: Int = 1) {
        viewModelScope.launch {
            //verificar si ya existe un item con ese producto
            val existingItems = cartDao.getCartItems(userId).first()
            val existingItem = existingItems.find { it.productId == productId }
            
            if (existingItem != null) {
                //si existe, actualizar la cantidad
                val updatedItem = existingItem.copy(cantidad = existingItem.cantidad + quantity)
                cartDao.updateCartItem(updatedItem)
            } else {
                //si no existe, crear uno nuevo
                val newCartItem = CartItem(
                    productId = productId,
                    cantidad = quantity,
                    userId = userId
                )
                cartDao.insertCartItem(newCartItem)
            }
        }
    }
    
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartDao.deleteCartItem(cartItem)
        }
    }
    
    fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                val updatedItem = cartItem.copy(cantidad = newQuantity)
                cartDao.updateCartItem(updatedItem)
            } else {
                cartDao.deleteCartItem(cartItem)
            }
        }
    }
    
    fun clearCart(userId: Long) {
        viewModelScope.launch {
            cartDao.clearCart(userId)
        }
    }
    
    suspend fun getTotalPrice(userId: Long): Double {
        val cartItems = cartDao.getCartItems(userId).first()
        return cartItems.sumOf { cartItem ->
            val product = productDao.getProductById(cartItem.productId)
            (product?.precio ?: 0.0) * cartItem.cantidad
        }
    }
    
    suspend fun confirmOrder(cartItems: List<CartItem>, userId: Long) {
        cartItems.forEach { cartItem ->
            val product = productDao.getProductById(cartItem.productId)
            if (product != null && product.stock >= cartItem.cantidad) {
                //crear la orden en la base de datos
                val order = Order(
                    productId = cartItem.productId,
                    userId = userId,
                    cantidad = cartItem.cantidad,
                    precioUnitario = product.precio,
                    total = product.precio * cartItem.cantidad,
                    estado = "confirmado"
                )
                orderDao.insertOrder(order)
                
                //reducir el stock
                val nuevoStock = product.stock - cartItem.cantidad
                productDao.updateStock(cartItem.productId, nuevoStock)
            }
        }
    }
}

data class CartItemWithProduct(
    val cartItem: CartItem,
    val product: Product
)
