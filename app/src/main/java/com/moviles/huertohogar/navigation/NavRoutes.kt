// Archivo: app/src/main/java/com.moviles.huertohogar/navigation/NavRoutes.kt

package com.moviles.huertohogar.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ExitToApp // Nuevo icono para Salir
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Archivo central de configuración de navegación.
 * Define las rutas y la estructura del menú inferior.
 */
object NavRoutes {
    const val HOME = "home"
    const val PRODUCTS = "products"
    const val CONTACT = "contact"
    const val LOGIN = "login"
    const val STORES = "stores"
    const val CART = "cart"
    const val PROFILE = "profile"
}

// Clase de datos para los ítems del menú
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

// Lista de navegación (Actualizada con Perfil)
val bottomNavItems = listOf(
    BottomNavItem(NavRoutes.HOME, Icons.Filled.Home, "Inicio"),
    BottomNavItem(NavRoutes.PRODUCTS, Icons.Filled.LocalGroceryStore, "Productos"),
    BottomNavItem(NavRoutes.STORES, Icons.Filled.LocationOn, "Sucursales"),
    // Agregamos el Perfil aquí:
    BottomNavItem(NavRoutes.PROFILE, Icons.Filled.Person, "Perfil"),
    // Cambiamos Login a "Salir" con icono de puerta:
    BottomNavItem(NavRoutes.LOGIN, Icons.Filled.ExitToApp, "Salir")
)