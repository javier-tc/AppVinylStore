package com.example.vinylstore.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Int,
    val userId: Long,
    val cantidad: Int,
    val precioUnitario: Double,
    val total: Double,
    val fecha: Long = System.currentTimeMillis(),
    val estado: String = "pendiente" //"pendiente", "confirmado", "entregado"
)

