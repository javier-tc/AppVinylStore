package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.ProductDao
import com.example.vinylstore.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productDao: ProductDao
) : ViewModel() {
    
    val products = productDao.getAllProducts()
    
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()
    
    init {
        initializeProducts()
    }
    
    private fun initializeProducts() {
        viewModelScope.launch {
            val count = productDao.productExists(1)
            if (count == 0) {
                val initialProducts = listOf(
                    Product(1, "Abbey Road", "The Beatles", 45000.0, 
                        "Álbum icónico de 1969", "Rock", "https://i.imgur.com/placeholder1.jpg", 10),
                    Product(2, "The Dark Side of the Moon", "Pink Floyd", 52000.0,
                        "Grabación revolucionaria de rock progresivo", "Progressive Rock", "https://i.imgur.com/placeholder2.jpg", 8),
                    Product(3, "Kind of Blue", "Miles Davis", 48000.0,
                        "Obra maestra del jazz modal", "Jazz", "https://i.imgur.com/placeholder3.jpg", 5),
                    Product(4, "Led Zeppelin IV", "Led Zeppelin", 47000.0,
                        "Álbum emblemático del rock", "Rock", "https://i.imgur.com/placeholder4.jpg", 12),
                    Product(5, "Miles Davis Quintet", "Miles Davis", 55000.0,
                        "Jazz legendario", "Jazz", "https://i.imgur.com/placeholder5.jpg", 7)
                )
                productDao.insertProducts(initialProducts)
            }
        }
    }
    
    fun selectProduct(product: Product) {
        _selectedProduct.value = product
    }
    
    fun clearSelection() {
        _selectedProduct.value = null
    }
    
    fun addProduct(product: Product) {
        viewModelScope.launch {
            productDao.insertProduct(product)
        }
    }
    
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productDao.updateProduct(product)
        }
    }
    
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productDao.deleteProduct(product)
        }
    }
    
    fun updateStock(productId: Int, stock: Int) {
        viewModelScope.launch {
            productDao.updateStock(productId, stock)
        }
    }
}
