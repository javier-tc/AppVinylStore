package com.example.vinylstore.repository

import com.example.vinylstore.data.model.*
import com.example.vinylstore.data.remote.dto.*

//mapeo de ProductDto a Product
fun ProductDto.toProduct(): Product {
    return Product(
        id = this.id,
        titulo = this.title,
        artista = this.artist,
        precio = this.price,
        descripcion = this.description,
        genero = this.genre,
        imagenUrl = this.imageUrl,
        stock = this.stock
    )
}

//mapeo de Product a CreateProductRequest
fun Product.toCreateProductRequest(): CreateProductRequest {
    return CreateProductRequest(
        title = this.titulo,
        artist = this.artista,
        genre = this.genero,
        description = this.descripcion,
        price = this.precio,
        stock = this.stock,
        imageUrl = this.imagenUrl
    )
}

//mapeo de UserProfileDto a User
fun UserProfileDto.toUser(): User {
    return User(
        id = this.id.toLong(),
        nombre = "${this.firstName} ${this.lastName}".trim(),
        email = this.email,
        password = "",
        rol = when (this.role.uppercase()) {
            "ADMIN" -> "administrador"
            else -> "cliente"
        }
    )
}

//mapeo de CartItemDto a CartItem
fun CartItemDto.toCartItem(): CartItem {
    return CartItem(
        id = this.id.toLong(),
        productId = this.productId,
        cantidad = this.quantity,
        userId = this.userId.toLong()
    )
}

//mapeo de OrderResponse a Order
fun com.example.vinylstore.data.remote.dto.OrderResponse.toOrder(): Order {
    //convertir fecha ISO string a timestamp Long
    val fechaTimestamp = try {
        val formatter = java.time.format.DateTimeFormatter.ISO_DATE_TIME
        val dateTime = java.time.LocalDateTime.parse(this.fecha, formatter)
        val zonedDateTime = dateTime.atZone(java.time.ZoneId.systemDefault())
        zonedDateTime.toInstant().toEpochMilli()
    } catch (e: Exception) {
        System.currentTimeMillis() //fallback a fecha actual si falla el parseo
    }
    
    //normalizar estado a minúsculas para el modelo local
    val estadoNormalizado = this.estado.lowercase()
    
    return Order(
        id = this.id,
        productId = this.productId,
        userId = this.userId,
        cantidad = this.cantidad,
        precioUnitario = this.precioUnitario,
        total = this.total,
        fecha = fechaTimestamp,
        estado = estadoNormalizado
    )
}


