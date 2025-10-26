package com.example.vinylstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.vinylstore.data.CartDao
import com.example.vinylstore.data.OrderDao
import com.example.vinylstore.data.ProductDao
import com.example.vinylstore.data.UserDao

class ViewModelFactory(
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val orderDao: OrderDao
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AuthViewModel::class.java -> AuthViewModel(userDao) as T
            ProductViewModel::class.java -> ProductViewModel(productDao) as T
            CartViewModel::class.java -> CartViewModel(cartDao, productDao, orderDao) as T
            OrderViewModel::class.java -> OrderViewModel(orderDao, productDao) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
