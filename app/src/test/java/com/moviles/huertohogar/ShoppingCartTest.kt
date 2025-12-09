// Archivo: app/src/test/java/com/moviles/huertohogar/domain/models/ShoppingCartTest.kt

package com.moviles.huertohogar.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ShoppingCartTest {


    private val manzana = Fruit(
        id = 1,
        name = "Manzana",
        price = 1000.0,
        unit = "kg",
        stock = 10,
        imageUrl = null
    )

    private val pera = Fruit(
        id = 2,
        name = "Pera",
        price = 2000.0,
        unit = "kg",
        stock = 5,
        imageUrl = null // <--- CAMBIO
    )

    // CONFIGURACIÓN INICIAL
    @Before
    fun setup() {
        ShoppingCart.clearCart()
    }

    // --- PRUEBA 1: AGREGAR PRODUCTO ---
    @Test
    fun `al agregar un producto nuevo, la cantidad debe ser 1`() {
        ShoppingCart.addItem(manzana)

        assertEquals(1, ShoppingCart.items.size)
        assertEquals(1, ShoppingCart.items[0].quantity)
    }

    // --- PRUEBA 2: AGREGAR DUPLICADO ---
    @Test
    fun `al agregar el mismo producto dos veces, la cantidad debe subir a 2`() {
        ShoppingCart.addItem(manzana)
        ShoppingCart.addItem(manzana)

        assertEquals(1, ShoppingCart.items.size)
        assertEquals(2, ShoppingCart.items[0].quantity)
    }

    // --- PRUEBA 3: CALCULAR TOTAL ---
    @Test
    fun `el total debe ser la suma de precio por cantidad`() {
        // 2 Manzanas (2 * 1000 = 2000)
        ShoppingCart.addItem(manzana)
        ShoppingCart.addItem(manzana)

        // 1 Pera (1 * 2000 = 2000)
        ShoppingCart.addItem(pera)

        // Total esperado = 4000
        val totalCalculado = ShoppingCart.getTotalPrice()

        assertEquals(4000.0, totalCalculado, 0.0)
    }

    // --- PRUEBA 4: ELIMINAR ÍTEM ---
    @Test
    fun `al eliminar un item con cantidad 1, debe desaparecer de la lista`() {
        ShoppingCart.addItem(manzana)

        val itemEnCarrito = ShoppingCart.items[0]
        ShoppingCart.removeItem(itemEnCarrito)

        assertTrue(ShoppingCart.items.isEmpty())
    }

    // --- PRUEBA 5: DECREMENTAR ÍTEM ---
    @Test
    fun `al eliminar un item con cantidad 2, debe bajar a 1`() {
        ShoppingCart.addItem(manzana)
        ShoppingCart.addItem(manzana) // Cantidad = 2

        val itemEnCarrito = ShoppingCart.items[0]
        ShoppingCart.removeItem(itemEnCarrito) // Eliminamos uno

        assertEquals(1, ShoppingCart.items.size)
        assertEquals(1, ShoppingCart.items[0].quantity)
    }

    // --- PRUEBA 6: VACIAR CARRITO ---
    @Test
    fun `limpiar carrito debe dejar la lista vacia y total en 0`() {
        ShoppingCart.addItem(manzana)
        ShoppingCart.addItem(pera)

        ShoppingCart.clearCart()

        assertEquals(0, ShoppingCart.items.size)
        assertEquals(0.0, ShoppingCart.getTotalPrice(), 0.0)
    }
}