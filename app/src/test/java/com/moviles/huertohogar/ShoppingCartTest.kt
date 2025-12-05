// Archivo: app/src/test/java/com/moviles/huertohogar/domain/models/ShoppingCartTest.kt

package com.moviles.huertohogar.domain.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ShoppingCartTest {

    // DATOS DE PRUEBA
    // Creamos frutas falsas para usar en las pruebas
    private val manzana = Fruit(
        id = 1,
        name = "Manzana",
        price = 1000.0,
        unit = "kg",
        stock = 10,
        imageResId = 0
    )

    private val pera = Fruit(
        id = 2,
        name = "Pera",
        price = 2000.0,
        unit = "kg",
        stock = 5,
        imageResId = 0
    )

    // CONFIGURACIÓN INICIAL
    // Esta función se ejecuta ANTES de cada @Test para asegurar que el carrito empiece vacío.
    @Before
    fun setup() {
        ShoppingCart.clearCart()
    }

    // --- PRUEBA 1: AGREGAR PRODUCTO ---
    @Test
    fun `al agregar un producto nuevo, la cantidad debe ser 1`() {
        ShoppingCart.addItem(manzana)

        // Verificamos que la lista tenga 1 elemento
        assertEquals(1, ShoppingCart.items.size)
        // Verificamos que la cantidad de ese elemento sea 1
        assertEquals(1, ShoppingCart.items[0].quantity)
    }

    // --- PRUEBA 2: AGREGAR DUPLICADO ---
    @Test
    fun `al agregar el mismo producto dos veces, la cantidad debe subir a 2`() {
        ShoppingCart.addItem(manzana)
        ShoppingCart.addItem(manzana) // Agregamos de nuevo

        // La lista debe seguir teniendo solo 1 ítem (no 2 filas)
        assertEquals(1, ShoppingCart.items.size)
        // Pero la cantidad debe ser 2
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

        // El tercer parámetro (0.0) es el margen de error permitido para doubles
        assertEquals(4000.0, totalCalculado, 0.0)
    }

    // --- PRUEBA 4: ELIMINAR ÍTEM ---
    @Test
    fun `al eliminar un item con cantidad 1, debe desaparecer de la lista`() {
        ShoppingCart.addItem(manzana)

        // Obtenemos el item del carrito para borrarlo
        val itemEnCarrito = ShoppingCart.items[0]
        ShoppingCart.removeItem(itemEnCarrito)

        // La lista debe estar vacía
        assertTrue(ShoppingCart.items.isEmpty())
    }

    // --- PRUEBA 5: DECREMENTAR ÍTEM ---
    @Test
    fun `al eliminar un item con cantidad 2, debe bajar a 1`() {
        ShoppingCart.addItem(manzana)
        ShoppingCart.addItem(manzana) // Cantidad = 2

        val itemEnCarrito = ShoppingCart.items[0]
        ShoppingCart.removeItem(itemEnCarrito) // Eliminamos uno

        // Debe seguir en la lista, pero con cantidad 1
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