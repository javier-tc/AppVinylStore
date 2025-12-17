package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vinylstore.repository.AuthRepository
import com.example.vinylstore.repository.CartRepository
import com.example.vinylstore.repository.MusicRepository
import com.example.vinylstore.repository.OrderRepository
import com.example.vinylstore.repository.ProductRepository
import com.example.vinylstore.repository.ProfileImageRepository

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val musicRepository: MusicRepository,
    private val profileImageRepository: ProfileImageRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(authRepository, profileImageRepository) as T
            ProductViewModel::class.java -> ProductViewModel(productRepository) as T
            CartViewModel::class.java -> CartViewModel(cartRepository, productRepository, orderRepository) as T
            OrderViewModel::class.java -> OrderViewModel(orderRepository, productRepository) as T
            MusicViewModel::class.java -> MusicViewModel(musicRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
