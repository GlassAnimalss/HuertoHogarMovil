// Archivo: app/src/main/java/com.moviles.huertohogar/MainActivity.kt

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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.moviles.huertohogar.data.database.AppDatabase
import com.moviles.huertohogar.data.repository.AuthRepository
import com.moviles.huertohogar.domain.auth.AuthState
import com.moviles.huertohogar.domain.auth.UserRole
import com.moviles.huertohogar.navigation.BottomNavItem
import com.moviles.huertohogar.navigation.NavRoutes
import com.moviles.huertohogar.navigation.bottomNavItems
import com.moviles.huertohogar.ui.common.AppHeader
import com.moviles.huertohogar.ui.screens.admin.AdminProductManagementScreen
import com.moviles.huertohogar.ui.screens.auth.LoginScreen
import com.moviles.huertohogar.ui.screens.auth.RegistrationScreen
import com.moviles.huertohogar.ui.screens.cart.CartScreen
import com.moviles.huertohogar.ui.screens.contact.ContactScreen
import com.moviles.huertohogar.ui.screens.home.HomeScreen
import com.moviles.huertohogar.ui.screens.products.ProductsScreen
import com.moviles.huertohogar.ui.screens.profile.ProfileScreen
import com.moviles.huertohogar.ui.screens.stores.StoresScreen
import com.moviles.huertohogar.ui.theme.HuertoHogarTheme
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
                println("FATAL ERROR ROOM INIT: ${e.message}")
            }
        }
    }

    if (!authState.isAuthenticated) {
        AuthNavigation(
            onLoginSuccess = { role, email ->
                authState = AuthState(isAuthenticated = true, role = role, userEmail = email)
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
                    onLogout = onLogout
                )
            }
        ) { paddingValues ->
            HuertoHogarNavHost(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                onLogout = onLogout,
                authState = authState
            )
        }
    }
}

@Composable
fun AuthNavigation(onLoginSuccess: (UserRole, String) -> Unit, navController: NavHostController) {
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
fun HuertoHogarNavHost(
    navController: NavHostController,
    modifier: Modifier,
    onLogout: () -> Unit,
    authState: AuthState
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = modifier
    ) {
        composable(NavRoutes.HOME) { HomeScreen() }
        composable(NavRoutes.PRODUCTS) { ProductsScreen() }
        composable(NavRoutes.CONTACT) { ContactScreen() }
        composable(NavRoutes.STORES) { StoresScreen() }

        composable(NavRoutes.PROFILE) {
            ProfileScreen(userEmail = authState.userEmail ?: "")
        }

        composable(NavRoutes.CART) {
            CartScreen(
                // PASAMOS EL EMAIL AL CARRITO
                userEmail = authState.userEmail ?: "",
                onPaymentSuccess = {
                    navController.popBackStack()
                    navController.navigate(NavRoutes.PRODUCTS) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
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
fun HuertoHogarBottomBar(navController: NavHostController, currentRoute: String?, onLogout: () -> Unit) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val isLogout = item.route == NavRoutes.LOGIN
            val isProducts = item.route == NavRoutes.PRODUCTS
            val isSecondaryRoute = currentRoute == NavRoutes.CART

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(if (isLogout) "Salir" else item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (isLogout) {
                        onLogout()
                    } else if (isProducts || currentRoute != item.route || isSecondaryRoute) {
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