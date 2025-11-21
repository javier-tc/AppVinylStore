package com.example.vinylstore.data.model

data class Product(
    val id: Int,
    val titulo: String,
    val artista: String,
    val precio: Double,
    val descripcion: String,
    val genero: String,
    val imagenUrl: String,
    val stock: Int = 0 //cantidad disponible
)

