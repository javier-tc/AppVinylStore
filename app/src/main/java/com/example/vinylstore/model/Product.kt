package com.example.vinylstore.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: Int,
    val titulo: String,
    val artista: String,
    val precio: Double,
    val descripcion: String,
    val genero: String,
    val imagenUrl: String,
    val stock: Int = 0 //cantidad disponible
)
