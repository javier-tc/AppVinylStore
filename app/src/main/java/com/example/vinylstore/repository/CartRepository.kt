package com.example.vinylstore.repository

import com.example.vinylstore.model.CartItem
import com.example.vinylstore.network.SessionManager
import com.example.vinylstore.network.api.CartApi
import com.example.vinylstore.network.dto.AddCartItemRequest
import com.example.vinylstore.network.dto.CartItemDto
import com.example.vinylstore.network.dto.CartTotalResponse
import com.example.vinylstore.network.dto.UpdateCartItemRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartRepository(
    private val cartApi: CartApi,
    private val sessionManager: SessionManager
) {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: Flow<List<CartItem>> = _cartItems.asStateFlow()
    
    suspend fun getCart(userId: Int): Result<List<CartItem>> {
        return try {
            val response = cartApi.getCart(userId)
            if (response.isSuccessful && response.body() != null) {
                val items = response.body()!!.map { it.toCartItem() }
                _cartItems.value = items
                Result.success(items)
            } else {
                Result.failure(Exception("Error al obtener carrito: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addItem(userId: Int, productId: Int, quantity: Int): Result<CartItem> {
        return try {
            val request = AddCartItemRequest(productId, quantity)
            val response = cartApi.addItem(userId, request)
            if (response.isSuccessful && response.body() != null) {
                val item = response.body()!!.toCartItem()
                _cartItems.update { it + item }
                Result.success(item)
            } else {
                Result.failure(Exception("Error al agregar item: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateItem(userId: Int, itemId: Int, quantity: Int): Result<CartItem> {
        return try {
            val request = UpdateCartItemRequest(quantity)
            val response = cartApi.updateItem(userId, itemId, request)
            if (response.isSuccessful && response.body() != null) {
                val item = response.body()!!.toCartItem()
                _cartItems.update { list ->
                    list.map { if (it.id.toInt() == itemId) item else it }
                }
                Result.success(item)
            } else {
                Result.failure(Exception("Error al actualizar item: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteItem(userId: Int, itemId: Int): Result<Unit> {
        return try {
            val response = cartApi.deleteItem(userId, itemId)
            if (response.isSuccessful) {
                _cartItems.update { it.filter { item -> item.id.toInt() != itemId } }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar item: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun clearCart(userId: Int): Result<Unit> {
        return try {
            val response = cartApi.clearCart(userId)
            if (response.isSuccessful) {
                _cartItems.value = emptyList()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al vaciar carrito: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCartTotal(userId: Int): Result<CartTotalResponse> {
        return try {
            val response = cartApi.getCartTotal(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener total: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getCartItemCount(userId: Int): Flow<Int> {
        return kotlinx.coroutines.flow.map(_cartItems.asStateFlow()) { items ->
            items.count { it.userId == userId.toLong() }
        }
    }
}

