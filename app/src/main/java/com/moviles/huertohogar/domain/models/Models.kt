// Archivo: app/src/main/java/com.moviles.huertohogar/domain/models/Models.kt

package com.moviles.huertohogar.domain.models


data class Fruit(
    val id: Int,
    val name: String,
    val price: Double,
    val unit: String,
    val stock: Int,
    val imageUrl: String?
)

// 2. Modelo de datos para un ítem en el carrito
data class CartItem(
    val fruit: Fruit,
    var quantity: Int
)