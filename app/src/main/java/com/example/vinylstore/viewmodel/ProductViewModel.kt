package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinylstore.data.model.Product
import com.example.vinylstore.repository.ProductRepository
import com.example.vinylstore.util.Validation
import com.example.vinylstore.util.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    val products = productRepository.products
    
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
        _saveProductState.value = SaveProductState.Initial
    }
    
    fun resetSaveState() {
        _saveProductState.value = SaveProductState.Initial
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
            
            val precioValor = estado.precio.toDoubleOrNull()
            val stockValor = estado.stock.toIntOrNull() ?: 0
            val imagenUrlFinal = estado.imagenUrl.ifBlank { "https://via.placeholder.com/300" }
            
            if (precioValor == null) {
                _saveProductState.value = SaveProductState.Error("El precio debe ser un número válido")
                return@launch
            }
            
            if (productId != null && productId <= 0) {
                _saveProductState.value = SaveProductState.Error("ID de producto inválido")
                return@launch
            }
            
            val newProduct = Product(
                id = productId ?: 0,
                titulo = estado.titulo,
                artista = estado.artista,
                precio = precioValor,
                descripcion = estado.descripcion,
                genero = estado.genero,
                imagenUrl = imagenUrlFinal,
                stock = stockValor
            )
            
            try {
                val result = if (productId == null) {
                    productRepository.createProduct(newProduct)
                } else {
                    productRepository.updateProduct(newProduct)
                }
                
                result.onSuccess {
                    _saveProductState.value = SaveProductState.Success
                }.onFailure { exception ->
                    _saveProductState.value = SaveProductState.Error(
                        exception.message ?: "Error al guardar producto"
                    )
                }
            } catch (e: Exception) {
                _saveProductState.value = SaveProductState.Error(
                    "Error inesperado: ${e.message ?: e.javaClass.simpleName}"
                )
            }
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
        loadProducts()
    }
    
    private fun loadProducts() {
        viewModelScope.launch {
            productRepository.getAllProducts().onFailure { exception ->
                //manejar error si es necesario
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
            productRepository.createProduct(product)
        }
    }
    
    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productRepository.updateProduct(product)
        }
    }
    
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product.id)
        }
    }
    
    fun updateStock(productId: Int, stock: Int) {
        viewModelScope.launch {
            productRepository.updateStock(productId, stock)
        }
    }
}
