package com.example.vinylstore.repository

import com.example.vinylstore.data.model.Product
import com.example.vinylstore.data.remote.api.ProductApi
import com.example.vinylstore.data.remote.dto.CreateProductRequest
import com.example.vinylstore.data.remote.dto.UpdateStockRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProductRepository(
    private val productApi: ProductApi
) {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: Flow<List<Product>> = _products.asStateFlow()
    
    suspend fun getAllProducts(genero: String? = null): Result<List<Product>> {
        return try {
            val response = productApi.getProducts(genero)
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!.map { it.toProduct() }
                _products.value = products
                Result.success(products)
            } else {
                Result.failure(Exception("Error al obtener productos: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProductById(id: Int): Result<Product> {
        return try {
            val response = productApi.getProductById(id)
            if (response.isSuccessful && response.body() != null) {
                val product = response.body()!!.toProduct()
                Result.success(product)
            } else {
                Result.failure(Exception("Error al obtener producto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createProduct(product: Product): Result<Product> {
        return try {
            val request = product.toCreateProductRequest()
            val response = productApi.createProduct(request)
            if (response.isSuccessful && response.body() != null) {
                val createdProduct = response.body()!!.toProduct()
                _products.update { it + createdProduct }
                Result.success(createdProduct)
            } else {
                Result.failure(Exception("Error al crear producto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateProduct(product: Product): Result<Product> {
        return try {
            val request = product.toCreateProductRequest()
            val response = productApi.updateProduct(product.id, request)
            if (response.isSuccessful && response.body() != null) {
                val updatedProduct = response.body()!!.toProduct()
                _products.update { list ->
                    list.map { if (it.id == updatedProduct.id) updatedProduct else it }
                }
                Result.success(updatedProduct)
            } else {
                Result.failure(Exception("Error al actualizar producto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteProduct(productId: Int): Result<Unit> {
        return try {
            val response = productApi.deleteProduct(productId)
            if (response.isSuccessful) {
                _products.update { it.filter { p -> p.id != productId } }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar producto: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateStock(productId: Int, stock: Int): Result<Product> {
        return try {
            val request = UpdateStockRequest(stock)
            val response = productApi.updateStock(productId, request)
            if (response.isSuccessful && response.body() != null) {
                val updatedProduct = response.body()!!.toProduct()
                _products.update { list ->
                    list.map { if (it.id == updatedProduct.id) updatedProduct else it }
                }
                Result.success(updatedProduct)
            } else {
                Result.failure(Exception("Error al actualizar stock: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


