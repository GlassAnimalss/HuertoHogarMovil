// Archivo: domain/models/ShoppingCart.kt

package com.moviles.huertohogar.domain.models

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

// Objeto singleton para manejar el estado del carrito globalmente (como Domain Service)
object ShoppingCart {

    val items: SnapshotStateList<CartItem> = mutableStateListOf()


    fun addItem(fruit: Fruit) {
        val existingItem = items.find { it.fruit.id == fruit.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            items.add(CartItem(fruit, 1))
        }
    }
    /** Elimina un ítem por completo o decrementa su cantidad. */
    fun removeItem(cartItem: CartItem) {
        if (cartItem.quantity > 1) {
            cartItem.quantity--
        } else {
            items.remove(cartItem) // Eliminar el ítem si solo queda 1
        }
    }

    /** Calcula el precio total de todos los productos en el carrito. */
    fun getTotalPrice(): Double {
        return items.sumOf { it.fruit.price * it.quantity }

    }



    /** Retorna el número total de unidades en el carrito. */
    fun getTotalItems(): Int {
        return items.sumOf { it.quantity }
    }

    // Función para simular un proceso de pago o vaciado
    fun clearCart() {
        items.clear()
    }
}