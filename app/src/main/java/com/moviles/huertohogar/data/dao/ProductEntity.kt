

package com.moviles.huertohogar.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.DrawableRes

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val unit: String, // e.g., "kg", "unidad"
    val stock: Int, // Campo editable por el Admin
    @DrawableRes val imageResId: Int // ID del recurso de imagen
)