package com.example.vinylstore.data.local.dao

import androidx.room.*
import com.example.vinylstore.data.local.entity.ProfileImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileImageDao {
    @Query("SELECT * FROM profile_images WHERE userId = :userId")
    fun getProfileImage(userId: Long): Flow<ProfileImageEntity?>
    
    @Query("SELECT * FROM profile_images WHERE userId = :userId")
    suspend fun getProfileImageSync(userId: Long): ProfileImageEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfileImage(profileImage: ProfileImageEntity)
    
    @Update
    suspend fun updateProfileImage(profileImage: ProfileImageEntity)
    
    @Delete
    suspend fun deleteProfileImage(profileImage: ProfileImageEntity)
    
    @Query("DELETE FROM profile_images WHERE userId = :userId")
    suspend fun deleteProfileImageByUserId(userId: Long)
}
