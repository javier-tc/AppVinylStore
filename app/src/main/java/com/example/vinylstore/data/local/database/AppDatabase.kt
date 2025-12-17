package com.example.vinylstore.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.vinylstore.data.local.dao.ProfileImageDao
import com.example.vinylstore.data.local.entity.ProfileImageEntity

@Database(
    entities = [ProfileImageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileImageDao(): ProfileImageDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vinylstore_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
