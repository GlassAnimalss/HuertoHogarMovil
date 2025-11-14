// Archivo: app/src/main/java/com.moviles.huertohogar/MainActivity.kt (Versión Final)

package com.moviles.huertohogar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moviles.huertohogar.domain.auth.AuthState
import com.moviles.huertohogar.domain.auth.UserRole
import com.moviles.huertohogar.navigation.NavRoutes
import com.moviles.huertohogar.data.database.AppDatabase
import com.moviles.huertohogar.data.repository.AuthRepository
import com.moviles.huertohogar.ui.common.AppHeader
import com.moviles.huertohogar.ui.screens.admin.AdminProductManagementScreen
import com.moviles.huertohogar.ui.screens.auth.LoginScreen
import com.moviles.huertohogar.ui.screens.auth.RegistrationScreen
import com.moviles.huertohogar.ui.screens.cart.CartScreen
import com.moviles.huertohogar.ui.screens.contact.ContactScreen
import com.moviles.huertohogar.ui.screens.home.HomeScreen
import com.moviles.huertohogar.ui.screens.products.ProductsScreen
import com.moviles.huertohogar.ui.screens.stores.StoresScreen
import com.moviles.huertohogar.ui.theme.HuertoHogarTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HuertoHogarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HuertoHogarApp()
                }
            }
        }
    }
}

// ----------------------------------------------------
// Estructura de Navegación del Menú Inferior (Cliente)
// ----------------------------------------------------

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

val clientNavItems = listOf(
    BottomNavItem(NavRoutes.HOME, Icons.Filled.Home, "Inicio"),
    BottomNavItem(NavRoutes.PRODUCTS, Icons.Filled.LocalGroceryStore, "Productos"),
    BottomNavItem(NavRoutes.STORES, Icons.Filled.LocationOn, "Sucursales"),
    BottomNavItem(NavRoutes.CONTACT, Icons.Filled.Email, "Contacto"),
    BottomNavItem(NavRoutes.LOGIN, Icons.Filled.ExitToApp, "Salir")
)

// ----------------------------------------------------
// Functión Principal de la Aplicación (Gestión de Roles)
// ----------------------------------------------------

@Composable
fun HuertoHogarApp() {
    val navController = rememberNavController()

    var authState by remember { mutableStateOf(AuthState()) }
    val onLogout: () -> Unit = { authState = AuthState() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember {
        AuthRepository(AppDatabase.getDatabase(context).userDao())
    }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                authRepository.createAdminIfNotExist()
            } catch (e: Exception) {
                println("FATAL ERROR ROOM INIT/CREATE ADMIN: ${e.message}")
            }
        }
    }

    if (!authState.isAuthenticated) {
        AuthNavigation(
            onLoginSuccess = { role ->
                authState = AuthState(isAuthenticated = true, role = role)
                navController.navigate(NavRoutes.HOME) {
                    popUpTo(NavRoutes.HOME) { inclusive = true }
                }
            },
            navController = navController
        )
    } else if (authState.role == UserRole.ADMIN) {
        AdminProductManagementScreen(onLogout = onLogout)
    } else {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            topBar = {
                AppHeader(onCartClick = {

                    navController.navigate(NavRoutes.CART) {
                        popUpTo(navController.currentDestination?.route ?: NavRoutes.HOME) {
                            saveState = true
                        }
                    }
                })
            },
            bottomBar = {
                HuertoHogarBottomBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    navItems = clientNavItems,
                    onLogout = onLogout
                )
            }
        ) { paddingValues ->
            HuertoHogarNavHost(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                onLogout = onLogout
            )
        }
    }
}



@Composable
fun AuthNavigation(onLoginSuccess: (UserRole) -> Unit, navController: NavHostController) {
    NavHost(navController = navController, startDestination = "login_screen") {
        composable("login_screen") {
            LoginScreen(
                onLoginSuccess = onLoginSuccess,
                onNavigateToRegister = { navController.navigate("register_screen") }
            )
        }
        composable("register_screen") {
            RegistrationScreen(
                onRegistrationSuccess = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}




@Composable
fun HuertoHogarNavHost(navController: NavHostController, modifier: Modifier, onLogout: () -> Unit) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier
    ) {
        composable(NavRoutes.HOME) { HomeScreen() }
        composable(NavRoutes.PRODUCTS) { ProductsScreen() }
        composable(NavRoutes.CONTACT) { ContactScreen() }
        composable(NavRoutes.STORES) { StoresScreen() }

        // RUTA DE CARRITO
        composable(NavRoutes.CART) {
            CartScreen(onPaymentSuccess = {

                navController.popBackStack()
            })
        }


        composable(NavRoutes.LOGIN) {
            LaunchedEffect(Unit) {
                onLogout()
            }
            Box(Modifier.fillMaxSize())
        }
    }
}


@Composable
fun HuertoHogarBottomBar(navController: NavHostController, currentRoute: String?, navItems: List<BottomNavItem>, onLogout: () -> Unit) {
    NavigationBar {
        navItems.forEach { item ->
            val isLogout = item.route == NavRoutes.LOGIN

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(if (isLogout) "Salir" else item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (isLogout) {
                        onLogout()
                    }

                    else if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}