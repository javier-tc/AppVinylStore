package com.example.vinylstore.data.model

data class User(
    val id: Long = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String = "cliente" //"cliente" o "administrador"
)

