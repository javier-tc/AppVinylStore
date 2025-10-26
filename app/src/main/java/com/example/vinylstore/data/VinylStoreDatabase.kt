package com.example.vinylstore.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vinylstore.model.CartItem
import com.example.vinylstore.model.Order
import com.example.vinylstore.model.Product
import com.example.vinylstore.model.User

@Database(
    entities = [User::class, Product::class, CartItem::class, Order::class],
    version = 2,
    exportSchema = false
)
abstract class VinylStoreDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    
    companion object {
        @Volatile
        private var INSTANCE: VinylStoreDatabase? = null
        
        fun getDatabase(context: Context): VinylStoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VinylStoreDatabase::class.java,
                    "vinylstore_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
