// Archivo: navigation/NavRoutes.kt

package com.moviles.huertohogar.navigation

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.graphics.vector.ImageVector

object NavRoutes {
    const val HOME = "home"
    const val PRODUCTS = "products"
    const val CONTACT = "contact"
    const val LOGIN = "login"
    const val STORES = "stores"

    const val CART = "cart"
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(NavRoutes.HOME, Icons.Filled.Home, "Inicio"),

    BottomNavItem(NavRoutes.PRODUCTS, Icons.Filled.LocalGroceryStore, "Productos"),
    BottomNavItem(NavRoutes.CONTACT, Icons.Filled.Email, "Contacto"),
    BottomNavItem(NavRoutes.LOGIN, Icons.Filled.Person, "Login"),
    BottomNavItem(NavRoutes.STORES, Icons.Filled.LocationOn, "Sucursales")
)