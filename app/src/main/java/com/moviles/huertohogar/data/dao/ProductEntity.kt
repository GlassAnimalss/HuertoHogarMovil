// Archivo: app/src/main/java/com.moviles.huertohogar/data/dao/ProductEntity.kt

package com.moviles.huertohogar.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = false) val id: Int, // Usamos el ID de la API
    val name: String,
    val price: Double,
    val unit: String,
    val stock: Int,
    val imageUrl: String? // <<< CAMBIO: Ahora guardamos la URL (texto)
)