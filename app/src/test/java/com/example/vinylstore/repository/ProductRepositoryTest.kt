package com.example.vinylstore.repository

import com.example.vinylstore.data.model.Product
import com.example.vinylstore.data.remote.api.ProductApi
import com.example.vinylstore.data.remote.dto.ProductDto
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

class ProductRepositoryTest {
    private lateinit var productApi: ProductApi
    private lateinit var repository: ProductRepository
    
    @Before
    fun setup() {
        productApi = mockk()
        repository = ProductRepository(productApi)
    }
    
    @Test
    fun `getAllProducts retorna lista de productos`() = runTest {
        val products = listOf(
            ProductDto(1, "Producto 1", "Artista 1", "Rock", "Desc", 19.99, 10, "url")
        )
        val response = Response.success(products)
        coEvery { productApi.getProducts(any()) } returns response
        
        val result = repository.getAllProducts()
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
    }
    
    @Test
    fun `getProductById retorna producto específico`() = runTest {
        val product = ProductDto(1, "Producto 1", "Artista 1", "Rock", "Desc", 19.99, 10, "url")
        val response = Response.success(product)
        coEvery { productApi.getProductById(any()) } returns response
        
        val result = repository.getProductById(1)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.id)
    }
    
    @Test
    fun `createProduct crea nuevo producto`() = runTest {
        val product = Product(0, "Nuevo", "Artista", 19.99, "Desc", "Rock", "url", 10)
        val productDto = ProductDto(1, "Nuevo", "Artista", "Rock", "Desc", 19.99, 10, "url")
        val response = Response.success(productDto)
        coEvery { productApi.createProduct(any()) } returns response
        
        val result = repository.createProduct(product)
        
        assertTrue(result.isSuccess)
        val products = repository.products.first()
        assertTrue(products.isNotEmpty())
    }
    
    @Test
    fun `updateProduct actualiza producto existente`() = runTest {
        val product = Product(1, "Actualizado", "Artista", 25.99, "Desc", "Rock", "url", 15)
        val productDto = ProductDto(1, "Actualizado", "Artista", "Rock", "Desc", 25.99, 15, "url")
        val response = Response.success(productDto)
        coEvery { productApi.updateProduct(any(), any()) } returns response
        
        val result = repository.updateProduct(product)
        
        assertTrue(result.isSuccess)
        assertEquals("Actualizado", result.getOrNull()?.titulo)
    }
    
    @Test
    fun `deleteProduct elimina producto`() = runTest {
        val response = Response.success<Unit>(Unit)
        coEvery { productApi.deleteProduct(any()) } returns response
        
        val result = repository.deleteProduct(1)
        
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `updateStock actualiza el stock`() = runTest {
        val productDto = ProductDto(1, "Producto", "Artista", "Rock", "Desc", 19.99, 20, "url")
        val response = Response.success(productDto)
        coEvery { productApi.updateStock(any(), any()) } returns response
        
        val result = repository.updateStock(1, 20)
        
        assertTrue(result.isSuccess)
        assertEquals(20, result.getOrNull()?.stock)
    }
    
    @Test
    fun `getAllProducts con género filtra productos`() = runTest {
        val products = listOf(
            ProductDto(1, "Producto 1", "Artista 1", "Rock", "Desc", 19.99, 10, "url")
        )
        val response = Response.success(products)
        coEvery { productApi.getProducts(any()) } returns response
        
        val result = repository.getAllProducts("Rock")
        
        assertTrue(result.isSuccess)
    }
    
    @Test
    fun `getAllProducts maneja error de respuesta`() = runTest {
        val response = Response.error<List<ProductDto>>(500, OkHttpResponseBody.create(null, ""))
        coEvery { productApi.getProducts(any()) } returns response
        
        val result = repository.getAllProducts()
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getProductById maneja error de respuesta`() = runTest {
        val response = Response.error<ProductDto>(404, OkHttpResponseBody.create(null, ""))
        coEvery { productApi.getProductById(any()) } returns response
        
        val result = repository.getProductById(1)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `createProduct maneja error de respuesta`() = runTest {
        val product = Product(0, "Nuevo", "Artista", 19.99, "Desc", "Rock", "url", 10)
        val response = Response.error<ProductDto>(400, OkHttpResponseBody.create(null, ""))
        coEvery { productApi.createProduct(any()) } returns response
        
        val result = repository.createProduct(product)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `updateProduct maneja error de respuesta`() = runTest {
        val product = Product(1, "Actualizado", "Artista", 25.99, "Desc", "Rock", "url", 15)
        val response = Response.error<ProductDto>(400, OkHttpResponseBody.create(null, ""))
        coEvery { productApi.updateProduct(any(), any()) } returns response
        
        val result = repository.updateProduct(product)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `deleteProduct maneja error de respuesta`() = runTest {
        val response = Response.error<Unit>(404, OkHttpResponseBody.create(null, ""))
        coEvery { productApi.deleteProduct(any()) } returns response
        
        val result = repository.deleteProduct(1)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `updateStock maneja error de respuesta`() = runTest {
        val response = Response.error<ProductDto>(400, OkHttpResponseBody.create(null, ""))
        coEvery { productApi.updateStock(any(), any()) } returns response
        
        val result = repository.updateStock(1, 20)
        
        assertTrue(result.isFailure)
    }
}


