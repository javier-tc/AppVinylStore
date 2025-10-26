package com.example.vinylstore.data

import androidx.room.*
import com.example.vinylstore.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>
    
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): Product?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)
    
    @Update
    suspend fun updateProduct(product: Product)
    
    @Delete
    suspend fun deleteProduct(product: Product)
    
    @Query("UPDATE products SET stock = :stock WHERE id = :productId")
    suspend fun updateStock(productId: Int, stock: Int)
    
    @Query("SELECT COUNT(*) FROM products WHERE id = :id")
    suspend fun productExists(id: Int): Int
}
