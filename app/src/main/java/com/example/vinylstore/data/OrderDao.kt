package com.example.vinylstore.data

import androidx.room.*
import com.example.vinylstore.model.Order
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY fecha DESC")
    fun getOrdersByUser(userId: Long): Flow<List<Order>>
    
    @Query("SELECT * FROM orders ORDER BY fecha DESC")
    fun getAllOrders(): Flow<List<Order>>
    
    @Query("SELECT * FROM orders WHERE productId = :productId")
    fun getOrdersByProduct(productId: Int): Flow<List<Order>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long
    
    @Update
    suspend fun updateOrder(order: Order)
    
    @Delete
    suspend fun deleteOrder(order: Order)
    
    @Query("UPDATE orders SET estado = :estado WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, estado: String)
}

