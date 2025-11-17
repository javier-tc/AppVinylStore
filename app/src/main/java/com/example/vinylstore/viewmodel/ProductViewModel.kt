package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.ProductDao
import com.example.vinylstore.model.Product
import com.example.vinylstore.util.Validation
import com.example.vinylstore.util.ValidationResult
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
    
    private val _productFormState = MutableStateFlow(
        ProductFormState(
            titulo = "",
            artista = "",
            precio = "",
            descripcion = "",
            genero = "",
            imagenUrl = "",
            stock = "",
            errores = ProductFormErrors()
        )
    )
    val productFormState: StateFlow<ProductFormState> = _productFormState
    
    private val _saveProductState = MutableStateFlow<SaveProductState>(SaveProductState.Initial)
    val saveProductState: StateFlow<SaveProductState> = _saveProductState
    
    fun initializeForm(product: Product?) {
        _productFormState.value = ProductFormState(
            titulo = product?.titulo ?: "",
            artista = product?.artista ?: "",
            precio = product?.precio?.toString() ?: "",
            descripcion = product?.descripcion ?: "",
            genero = product?.genero ?: "",
            imagenUrl = product?.imagenUrl ?: "",
            stock = product?.stock?.toString() ?: "",
            errores = ProductFormErrors()
        )
    }
    
    fun onTituloChange(titulo: String) {
        _productFormState.value = _productFormState.value.copy(
            titulo = titulo,
            errores = _productFormState.value.errores.copy(
                titulo = validateProductTitle(titulo).let {
                    when (it) {
                        is ValidationResult.Success -> null
                        is ValidationResult.Error -> it.message
                    }
                }
            )
        )
    }
    
    fun onArtistaChange(artista: String) {
        _productFormState.value = _productFormState.value.copy(
            artista = artista,
            errores = _productFormState.value.errores.copy(
                artista = validateProductArtist(artista).let {
                    when (it) {
                        is ValidationResult.Success -> null
                        is ValidationResult.Error -> it.message
                    }
                }
            )
        )
    }
    
    fun onPrecioChange(precio: String) {
        _productFormState.value = _productFormState.value.copy(
            precio = precio,
            errores = _productFormState.value.errores.copy(
                precio = validateProductPrice(precio).let {
                    when (it) {
                        is ValidationResult.Success -> null
                        is ValidationResult.Error -> it.message
                    }
                }
            )
        )
    }
    
    fun onDescripcionChange(descripcion: String) {
        _productFormState.value = _productFormState.value.copy(
            descripcion = descripcion
        )
    }
    
    fun onGeneroChange(genero: String) {
        _productFormState.value = _productFormState.value.copy(
            genero = genero
        )
    }
    
    fun onImagenUrlChange(imagenUrl: String) {
        _productFormState.value = _productFormState.value.copy(
            imagenUrl = imagenUrl,
            errores = _productFormState.value.errores.copy(
                imagenUrl = validateImageUrl(imagenUrl).let {
                    when (it) {
                        is ValidationResult.Success -> null
                        is ValidationResult.Error -> it.message
                    }
                }
            )
        )
    }
    
    fun onStockChange(stock: String) {
        _productFormState.value = _productFormState.value.copy(
            stock = stock,
            errores = _productFormState.value.errores.copy(
                stock = validateProductStock(stock).let {
                    when (it) {
                        is ValidationResult.Success -> null
                        is ValidationResult.Error -> it.message
                    }
                }
            )
        )
    }
    
    private fun validateProductTitle(titulo: String): ValidationResult {
        return Validation.validateProductTitle(titulo)
    }
    
    private fun validateProductArtist(artista: String): ValidationResult {
        return Validation.validateProductArtist(artista)
    }
    
    private fun validateProductPrice(precio: String): ValidationResult {
        return Validation.validateProductPrice(precio)
    }
    
    private fun validateProductStock(stock: String): ValidationResult {
        return Validation.validateProductStock(stock)
    }
    
    private fun validateImageUrl(url: String): ValidationResult {
        return Validation.validateImageUrl(url)
    }
    
    fun saveProduct(productId: Int?) {
        viewModelScope.launch {
            val estado = _productFormState.value
            
            val tituloValidation = validateProductTitle(estado.titulo)
            val artistaValidation = validateProductArtist(estado.artista)
            val precioValidation = validateProductPrice(estado.precio)
            val stockValidation = validateProductStock(estado.stock)
            val imagenUrlValidation = validateImageUrl(estado.imagenUrl)
            
            val nuevosErrores = ProductFormErrors(
                titulo = when (tituloValidation) {
                    is ValidationResult.Success -> null
                    is ValidationResult.Error -> tituloValidation.message
                },
                artista = when (artistaValidation) {
                    is ValidationResult.Success -> null
                    is ValidationResult.Error -> artistaValidation.message
                },
                precio = when (precioValidation) {
                    is ValidationResult.Success -> null
                    is ValidationResult.Error -> precioValidation.message
                },
                stock = when (stockValidation) {
                    is ValidationResult.Success -> null
                    is ValidationResult.Error -> stockValidation.message
                },
                imagenUrl = when (imagenUrlValidation) {
                    is ValidationResult.Success -> null
                    is ValidationResult.Error -> imagenUrlValidation.message
                }
            )
            
            _productFormState.value = estado.copy(errores = nuevosErrores)
            
            if (nuevosErrores.titulo != null || nuevosErrores.artista != null || 
                nuevosErrores.precio != null || nuevosErrores.stock != null || 
                nuevosErrores.imagenUrl != null) {
                return@launch
            }
            
            _saveProductState.value = SaveProductState.Loading
            
            val precioValor = estado.precio.toDouble()
            val stockValor = estado.stock.toIntOrNull() ?: 0
            val imagenUrlFinal = estado.imagenUrl.ifBlank { "https://via.placeholder.com/300" }
            
            val newProduct = Product(
                id = productId ?: (System.currentTimeMillis().toInt() % 100000),
                titulo = estado.titulo,
                artista = estado.artista,
                precio = precioValor,
                descripcion = estado.descripcion,
                genero = estado.genero,
                imagenUrl = imagenUrlFinal,
                stock = stockValor
            )
            
            if (productId == null) {
                addProduct(newProduct)
            } else {
                updateProduct(newProduct)
            }
            
            _saveProductState.value = SaveProductState.Success
        }
    }
    
    data class ProductFormState(
        val titulo: String,
        val artista: String,
        val precio: String,
        val descripcion: String,
        val genero: String,
        val imagenUrl: String,
        val stock: String,
        val errores: ProductFormErrors
    )
    
    data class ProductFormErrors(
        val titulo: String? = null,
        val artista: String? = null,
        val precio: String? = null,
        val stock: String? = null,
        val imagenUrl: String? = null
    )
    
    sealed class SaveProductState {
        object Initial : SaveProductState()
        object Loading : SaveProductState()
        object Success : SaveProductState()
        data class Error(val message: String) : SaveProductState()
    }
    
    init {
        initializeProducts()
    }
    
    private fun initializeProducts() {
        viewModelScope.launch {
            val count = productDao.productExists(1)
            if (count == 0) {
                val initialProducts = listOf(
                    Product(1, "Abbey Road", "The Beatles", 45000.0, 
                        "Álbum icónico de 1969", "Rock", "https://th.bing.com/th/id/R.9b5bfaa303595bec7d186032fdf46282?rik=P%2bmzEURyBGZvDQ&pid=ImgRaw&r=0", 10),
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
