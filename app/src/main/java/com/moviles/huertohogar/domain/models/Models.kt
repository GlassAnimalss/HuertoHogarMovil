// Archivo: app/src/main/java/com.moviles.huertohogar/domain/models/Models.kt

package com.moviles.huertohogar.domain.models

import androidx.annotation.DrawableRes

// 1. Modelo de datos para un producto
data class Fruit(
    val id: Int,
    val name: String,
    val price: Double,
    val unit: String,
    val stock: Int, // <<< NUEVO CAMPO: Necesario para validar disponibilidad
    @DrawableRes val imageResId: Int
)

// 2. Modelo de datos para un ítem en el carrito
data class CartItem(
    val fruit: Fruit,
    var quantity: Int
)