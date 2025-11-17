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
                        "Álbum icónico de 1969", "Rock", "https://th.bing.com/th/id/R.b9e21ed4bb3b6f8f887596d0aa0bdfd0?rik=gGo8lzF1jNmrVA&riu=http%3a%2f%2fimg.wennermedia.com%2f920-width%2frs-136803-0921fbfa76c66953268f4bdeee9410ef7ef02536.jpg&ehk=rWIlAmxhRw462ZiSZjtOWbhj2Wc%2fUdlNdtfKHaQMHn0%3d&risl=&pid=ImgRaw&r=0", 10),
                    Product(2, "The Dark Side of the Moon", "Pink Floyd", 52000.0,
                        "Grabación revolucionaria de rock progresivo", "Progressive Rock", "https://tse4.mm.bing.net/th/id/OIP.pNbqWq1EjFsAtHnJwKoYcwHaHa?rs=1&pid=ImgDetMain&o=7&rm=3", 8),
                    Product(3, "Kind of Blue", "Miles Davis", 48000.0,
                        "Obra maestra del jazz modal", "Jazz", "https://tse3.mm.bing.net/th/id/OIP.bq5eSwrarMlhhH3fRZmQHwHaGs?rs=1&pid=ImgDetMain&o=7&rm=3", 5),
                    Product(4, "Led Zeppelin IV", "Led Zeppelin", 47000.0,
                        "Álbum emblemático del rock", "Rock", "https://http2.mlstatic.com/D_NQ_NP_908718-MLB51428359896_092022-O.webp", 12),
                    Product(5, "Miles Davis Quintet", "Miles Davis", 55000.0,
                        "Jazz legendario", "Jazz", "https://th.bing.com/th/id/R.e062b28d9a1590221ecc67ba727aad28?rik=e316YypZCfcCqg&riu=http%3a%2f%2fwww.progarchives.com%2fprogressive_rock_discography_covers%2f3906%2fcover_51772062016_r.jpg&ehk=M594PasbB9Xo2OtLA%2f%2fh9SgN7ffAehxTtAQWF%2bjbZj4%3d&risl=&pid=ImgRaw&r=0", 7)
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
