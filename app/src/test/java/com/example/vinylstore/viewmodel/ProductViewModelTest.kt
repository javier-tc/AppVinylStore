package com.example.vinylstore.viewmodel

import com.example.vinylstore.data.model.Product
import com.example.vinylstore.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {
    private lateinit var productRepository: ProductRepository
    private lateinit var viewModel: ProductViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        productRepository = mockk()
        every { productRepository.products } returns MutableStateFlow(emptyList())
        coEvery { productRepository.getAllProducts() } returns Result.success(emptyList())
        viewModel = ProductViewModel(productRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `onTituloChange actualiza el título`() {
        viewModel.onTituloChange("Nuevo Título")
        val state = viewModel.productFormState.value
        assertEquals("Nuevo Título", state.titulo)
    }
    
    @Test
    fun `onPrecioChange actualiza el precio`() {
        viewModel.onPrecioChange("29.99")
        val state = viewModel.productFormState.value
        assertEquals("29.99", state.precio)
    }
    
    @Test
    fun `initializeForm carga datos del producto existente`() {
        val product = Product(1, "Título", "Artista", 19.99, "Descripción", "Rock", "url", 10)
        
        viewModel.initializeForm(product)
        val state = viewModel.productFormState.value
        
        assertEquals("Título", state.titulo)
        assertEquals("Artista", state.artista)
        assertEquals("19.99", state.precio)
        assertEquals("Descripción", state.descripcion)
        assertEquals("Rock", state.genero)
        assertEquals("url", state.imagenUrl)
        assertEquals("10", state.stock)
    }
    
    @Test
    fun `initializeForm con null inicializa campos vacíos`() {
        viewModel.initializeForm(null)
        val state = viewModel.productFormState.value
        
        assertEquals("", state.titulo)
        assertEquals("", state.artista)
        assertEquals("", state.precio)
    }
    
    @Test
    fun `saveProduct crea nuevo producto cuando productId es null`() = runTest {
        val newProduct = Product(0, "Nuevo", "Artista", 19.99, "", "Rock", "https://example.com/image.jpg", 10)
        coEvery { productRepository.createProduct(any()) } returns Result.success(newProduct)
        every { productRepository.products } returns MutableStateFlow(emptyList())
        
        viewModel.onTituloChange("Nuevo")
        viewModel.onArtistaChange("Artista")
        viewModel.onPrecioChange("19.99")
        viewModel.onGeneroChange("Rock")
        viewModel.onStockChange("10")
        viewModel.onImagenUrlChange("https://example.com/image.jpg")
        viewModel.saveProduct(null)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { productRepository.createProduct(any()) }
    }
    
    @Test
    fun `saveProduct actualiza producto existente cuando productId no es null`() = runTest {
        val updatedProduct = Product(1, "Actualizado", "Artista", 25.99, "", "Rock", "https://example.com/image.jpg", 15)
        coEvery { productRepository.updateProduct(any()) } returns Result.success(updatedProduct)
        every { productRepository.products } returns MutableStateFlow(emptyList())
        
        viewModel.onTituloChange("Actualizado")
        viewModel.onArtistaChange("Artista")
        viewModel.onPrecioChange("25.99")
        viewModel.onGeneroChange("Rock")
        viewModel.onStockChange("15")
        viewModel.onImagenUrlChange("https://example.com/image.jpg")
        viewModel.saveProduct(1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { productRepository.updateProduct(any()) }
    }
    
    @Test
    fun `deleteProduct elimina el producto`() = runTest {
        coEvery { productRepository.deleteProduct(any()) } returns Result.success(Unit)
        
        val product = Product(1, "Título", "Artista", 19.99, "", "Rock", "", 10)
        viewModel.deleteProduct(product)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { productRepository.deleteProduct(1) }
    }
    
    @Test
    fun `selectProduct establece el producto seleccionado`() {
        val product = Product(1, "Título", "Artista", 19.99, "", "Rock", "", 10)
        
        viewModel.selectProduct(product)
        
        assertEquals(product, viewModel.selectedProduct.value)
    }
    
    @Test
    fun `clearSelection limpia la selección`() {
        val product = Product(1, "Título", "Artista", 19.99, "", "Rock", "", 10)
        viewModel.selectProduct(product)
        viewModel.clearSelection()
        
        assertNull(viewModel.selectedProduct.value)
    }
    
    @Test
    fun `onArtistaChange actualiza el artista`() {
        viewModel.onArtistaChange("Nuevo Artista")
        val state = viewModel.productFormState.value
        assertEquals("Nuevo Artista", state.artista)
    }
    
    @Test
    fun `onDescripcionChange actualiza la descripción`() {
        viewModel.onDescripcionChange("Nueva descripción")
        val state = viewModel.productFormState.value
        assertEquals("Nueva descripción", state.descripcion)
    }
    
    @Test
    fun `onGeneroChange actualiza el género`() {
        viewModel.onGeneroChange("Jazz")
        val state = viewModel.productFormState.value
        assertEquals("Jazz", state.genero)
    }
    
    @Test
    fun `onImagenUrlChange actualiza la URL de imagen`() {
        viewModel.onImagenUrlChange("https://example.com/image.jpg")
        val state = viewModel.productFormState.value
        assertEquals("https://example.com/image.jpg", state.imagenUrl)
    }
    
    @Test
    fun `onStockChange actualiza el stock`() {
        viewModel.onStockChange("15")
        val state = viewModel.productFormState.value
        assertEquals("15", state.stock)
    }
    
    @Test
    fun `saveProduct no continúa si hay errores de validación`() = runTest {
        viewModel.onTituloChange("")
        viewModel.onArtistaChange("")
        viewModel.onPrecioChange("")
        viewModel.saveProduct(null)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify(exactly = 0) { productRepository.createProduct(any()) }
    }
    
    @Test
    fun `saveProduct maneja error al crear producto`() = runTest {
        coEvery { productRepository.createProduct(any()) } returns Result.failure(Exception("Error al crear"))
        every { productRepository.products } returns MutableStateFlow(emptyList())
        
        viewModel.onTituloChange("Nuevo")
        viewModel.onArtistaChange("Artista")
        viewModel.onPrecioChange("19.99")
        viewModel.onGeneroChange("Rock")
        viewModel.onStockChange("10")
        viewModel.onImagenUrlChange("https://example.com/image.jpg")
        viewModel.saveProduct(null)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.saveProductState.value
        assertTrue(state is ProductViewModel.SaveProductState.Error)
    }
    
    @Test
    fun `saveProduct maneja error al actualizar producto`() = runTest {
        coEvery { productRepository.updateProduct(any()) } returns Result.failure(Exception("Error al actualizar"))
        every { productRepository.products } returns MutableStateFlow(emptyList())
        
        viewModel.onTituloChange("Actualizado")
        viewModel.onArtistaChange("Artista")
        viewModel.onPrecioChange("25.99")
        viewModel.onGeneroChange("Rock")
        viewModel.onStockChange("15")
        viewModel.onImagenUrlChange("https://example.com/image.jpg")
        viewModel.saveProduct(1)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.saveProductState.value
        assertTrue(state is ProductViewModel.SaveProductState.Error)
    }
    
    @Test
    fun `onTituloChange valida título inválido`() {
        viewModel.onTituloChange("")
        val state = viewModel.productFormState.value
        assertNotNull(state.errores.titulo)
    }
    
    @Test
    fun `onArtistaChange valida artista inválido`() {
        viewModel.onArtistaChange("")
        val state = viewModel.productFormState.value
        assertNotNull(state.errores.artista)
    }
    
    @Test
    fun `onPrecioChange valida precio inválido`() {
        viewModel.onPrecioChange("abc")
        val state = viewModel.productFormState.value
        assertNotNull(state.errores.precio)
    }
    
    @Test
    fun `onStockChange valida stock inválido`() {
        viewModel.onStockChange("-1")
        val state = viewModel.productFormState.value
        assertNotNull(state.errores.stock)
    }
    
    @Test
    fun `onImagenUrlChange valida URL inválida`() {
        viewModel.onImagenUrlChange("not-a-url")
        val state = viewModel.productFormState.value
        assertNotNull(state.errores.imagenUrl)
    }
    
    @Test
    fun `updateProduct actualiza producto`() = runTest {
        val product = Product(1, "Título", "Artista", 19.99, "", "Rock", "", 10)
        coEvery { productRepository.updateProduct(any()) } returns Result.success(product)
        
        viewModel.updateProduct(product)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { productRepository.updateProduct(product) }
    }
    
    @Test
    fun `addProduct crea producto`() = runTest {
        val product = Product(0, "Nuevo", "Artista", 19.99, "", "Rock", "", 10)
        coEvery { productRepository.createProduct(any()) } returns Result.success(product)
        
        viewModel.addProduct(product)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { productRepository.createProduct(product) }
    }
    
    @Test
    fun `updateStock actualiza el stock`() = runTest {
        coEvery { productRepository.updateStock(any(), any()) } returns Result.success(
            Product(1, "Título", "Artista", 19.99, "", "Rock", "", 15)
        )
        
        viewModel.updateStock(1, 15)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { productRepository.updateStock(1, 15) }
    }
}


