package com.example.vinylstore.repository

import com.example.vinylstore.data.local.dao.ProfileImageDao
import com.example.vinylstore.data.local.entity.ProfileImageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileImageRepository(
    private val profileImageDao: ProfileImageDao
) {
    fun getProfileImageUri(userId: Long): Flow<String?> {
        return profileImageDao.getProfileImage(userId).map { it?.imageUri }
    }
    
    suspend fun getProfileImageUriSync(userId: Long): String? {
        return profileImageDao.getProfileImageSync(userId)?.imageUri
    }
    
    suspend fun saveProfileImage(userId: Long, imageUri: String) {
        profileImageDao.insertProfileImage(
            ProfileImageEntity(
                userId = userId,
                imageUri = imageUri
            )
        )
    }
    
    suspend fun updateProfileImage(userId: Long, imageUri: String) {
        val existingImage = profileImageDao.getProfileImageSync(userId)
        if (existingImage != null) {
            profileImageDao.updateProfileImage(
                ProfileImageEntity(
                    userId = userId,
                    imageUri = imageUri
                )
            )
        }
    }
    
    suspend fun deleteProfileImage(userId: Long) {
        profileImageDao.deleteProfileImageByUserId(userId)
    }
}
