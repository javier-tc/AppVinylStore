package com.example.vinylstore.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_images")
data class ProfileImageEntity(
    @PrimaryKey
    val userId: Long,
    val imageUri: String
)
