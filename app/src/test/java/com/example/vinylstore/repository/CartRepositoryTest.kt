package com.example.vinylstore.repository

import com.example.vinylstore.data.remote.api.CartApi
import com.example.vinylstore.data.remote.dto.CartItemDto
import com.example.vinylstore.data.remote.dto.CartTotalResponse
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

class CartRepositoryTest {
    private lateinit var cartApi: CartApi
    private lateinit var repository: CartRepository
    
    @Before
    fun setup() {
        cartApi = mockk()
        repository = CartRepository(cartApi)
    }
    
    @Test
    fun `getCart retorna lista de items`() = runTest {
        val cartItems = listOf(
            CartItemDto(1, 1, 1, 2, 10.0, 20.0)
        )
        val response = Response.success(cartItems)
        coEvery { cartApi.getCart(any()) } returns response
        
        val result = repository.getCart(1)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }
    
    @Test
    fun `addItem agrega nuevo item al carrito`() = runTest {
        val cartItem = CartItemDto(1, 1, 1, 1, 10.0, 10.0)
        val response = Response.success(cartItem)
        coEvery { cartApi.addItem(any(), any()) } returns response
        
        val result = repository.addItem(1, 1, 1)
        
        assertTrue(result.isSuccess)
        val items = repository.cartItems.first()
        assertTrue(items.isNotEmpty())
    }
    
    @Test
    fun `updateItem actualiza cantidad del item`() = runTest {
        val cartItem = CartItemDto(1, 1, 1, 3, 10.0, 30.0)
        val response = Response.success(cartItem)
        coEvery { cartApi.updateItem(any(), any(), any()) } returns response
        
        val result = repository.updateItem(1, 1, 3)
        
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.cantidad)
    }
    
    @Test
    fun `deleteItem elimina item del carrito`() = runTest {
        val response = Response.success<Unit>(Unit)
        coEvery { cartApi.deleteItem(any(), any()) } returns response
        
        val result = repository.deleteItem(1, 1)
        
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `clearCart vacía el carrito`() = runTest {
        val response = Response.success<Unit>(Unit)
        coEvery { cartApi.clearCart(any()) } returns response
        
        val result = repository.clearCart(1)
        
        assertTrue(result.isSuccess)
        val items = repository.cartItems.first()
        assertTrue(items.isEmpty())
    }
    
    @Test
    fun `getCartTotal calcula el total correctamente`() = runTest {
        val totalResponse = CartTotalResponse(1, emptyList(), 50.0, 0)
        val response = Response.success(totalResponse)
        coEvery { cartApi.getCartTotal(any()) } returns response
        
        val result = repository.getCartTotal(1)
        
        assertTrue(result.isSuccess)
        assertEquals(50.0, result.getOrNull()?.total ?: 0.0, 0.01)
    }
    
    @Test
    fun `getCartItemCount retorna cantidad correcta`() = runTest {
        val cartItems = listOf(
            com.example.vinylstore.data.model.CartItem(1, 1, 1, 1),
            com.example.vinylstore.data.model.CartItem(2, 1, 2, 1)
        )
        val response = Response.success(cartItems.map { 
            com.example.vinylstore.data.remote.dto.CartItemDto(it.id.toInt(), it.userId.toInt(), it.productId, it.cantidad, 10.0, 10.0 * it.cantidad)
        })
        coEvery { cartApi.getCart(any()) } returns response
        
        repository.getCart(1)
        val count = repository.getCartItemCount(1).first()
        assertEquals(2, count)
    }
    
    @Test
    fun `getCart maneja error de respuesta`() = runTest {
        val response = Response.error<List<CartItemDto>>(500, OkHttpResponseBody.create(null, ""))
        coEvery { cartApi.getCart(any()) } returns response
        
        val result = repository.getCart(1)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `addItem maneja error de respuesta`() = runTest {
        val response = Response.error<CartItemDto>(400, OkHttpResponseBody.create(null, ""))
        coEvery { cartApi.addItem(any(), any()) } returns response
        
        val result = repository.addItem(1, 1, 1)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `updateItem maneja error de respuesta`() = runTest {
        val response = Response.error<CartItemDto>(400, OkHttpResponseBody.create(null, ""))
        coEvery { cartApi.updateItem(any(), any(), any()) } returns response
        
        val result = repository.updateItem(1, 1, 3)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `deleteItem maneja error de respuesta`() = runTest {
        val response = Response.error<Unit>(404, OkHttpResponseBody.create(null, ""))
        coEvery { cartApi.deleteItem(any(), any()) } returns response
        
        val result = repository.deleteItem(1, 1)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `clearCart maneja error de respuesta`() = runTest {
        val response = Response.error<Unit>(500, OkHttpResponseBody.create(null, ""))
        coEvery { cartApi.clearCart(any()) } returns response
        
        val result = repository.clearCart(1)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getCartTotal maneja error de respuesta`() = runTest {
        val response = Response.error<CartTotalResponse>(500, OkHttpResponseBody.create(null, ""))
        coEvery { cartApi.getCartTotal(any()) } returns response
        
        val result = repository.getCartTotal(1)
        
        assertTrue(result.isFailure)
    }
}

