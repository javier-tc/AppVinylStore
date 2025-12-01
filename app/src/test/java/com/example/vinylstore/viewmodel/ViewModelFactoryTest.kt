package com.example.vinylstore.viewmodel

import com.example.vinylstore.repository.AuthRepository
import com.example.vinylstore.repository.CartRepository
import com.example.vinylstore.repository.MusicRepository
import com.example.vinylstore.repository.OrderRepository
import com.example.vinylstore.repository.ProductRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ViewModelFactoryTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var musicRepository: MusicRepository
    private lateinit var factory: ViewModelFactory
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk(relaxed = true)
        productRepository = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        musicRepository = mockk(relaxed = true)
        
        //configurar mocks necesarios para los constructores
        every { authRepository.currentUser } returns MutableStateFlow(null)
        every { productRepository.products } returns MutableStateFlow(emptyList())
        every { orderRepository.orders } returns MutableStateFlow(emptyList())
        coEvery { productRepository.getAllProducts(any()) } returns Result.success(emptyList())
        coEvery { orderRepository.getMyOrders() } returns Result.success(emptyList())
        
        factory = ViewModelFactory(
            authRepository,
            productRepository,
            cartRepository,
            orderRepository,
            musicRepository
        )
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `create retorna AuthViewModel cuando se solicita`() {
        val viewModel = factory.create(AuthViewModel::class.java)
        assertTrue(viewModel is AuthViewModel)
    }
    
    @Test
    fun `create retorna ProductViewModel cuando se solicita`() {
        val viewModel = factory.create(ProductViewModel::class.java)
        assertTrue(viewModel is ProductViewModel)
    }
    
    @Test
    fun `create retorna CartViewModel cuando se solicita`() {
        val viewModel = factory.create(CartViewModel::class.java)
        assertTrue(viewModel is CartViewModel)
    }
    
    @Test
    fun `create retorna OrderViewModel cuando se solicita`() {
        val viewModel = factory.create(OrderViewModel::class.java)
        assertTrue(viewModel is OrderViewModel)
    }
    
    @Test
    fun `create retorna MusicViewModel cuando se solicita`() {
        val viewModel = factory.create(MusicViewModel::class.java)
        assertTrue(viewModel is MusicViewModel)
    }
    
    @Test
    fun `create lanza excepción para ViewModel desconocido`() {
        try {
            factory.create(Any::class.java as Class<androidx.lifecycle.ViewModel>)
            fail("Debería lanzar IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            //esperado
            assertTrue(true)
        }
    }
}

