package com.moviles.huertohogar.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

import com.moviles.huertohogar.data.database.AppDatabase
import com.moviles.huertohogar.data.repository.AuthRepository
import com.moviles.huertohogar.domain.auth.UserRole
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale
import com.moviles.huertohogar.R
import androidx.compose.ui.res.painterResource

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit, // Navegación al éxito, enviando el rol
    onNavigateToRegister: () -> Unit // Navegación a la pantalla de registro
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // --- Instancias para Room y Coroutines ---
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Obtenemos el Repositorio de Autenticación
    val authRepository = remember {
        AuthRepository(AppDatabase.getDatabase(context).userDao())
    }
    // -----------------------------------------

    // 1. Inicialización de Datos: Protegido contra Crash
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // Esto crea al Admin si no existe en la DB
                authRepository.createAdminIfNotExist()
            } catch (e: Exception) {
                // Captura el error de Room si la inicialización falló
                println("FATAL ERROR ROOM INIT/CREATE ADMIN: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ---------------------------------------------
        // <<< LOGO DE HUERTOHOGAR >>>
        // ---------------------------------------------
        Image(
            painter = painterResource(id = R.drawable.huerto_hogar_2),
            contentDescription = "Logo HuertoHogar Login",
            modifier = Modifier
                .height(350.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )
        // ---------------------------------------------

        Text(
            text = "INICIO DE SESIÓN",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // ---------------------------------------------
        // <<< CAMPOS DE TEXTO >>>
        // ---------------------------------------------
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
        // ---------------------------------------------

        // Botón de Inicio de Sesión (Lógica REAL de Autenticación)
        Button(
            onClick = {
                scope.launch {
                    try {
                        val role = authRepository.loginUser(email, password)

                        if (role != UserRole.UNAUTHENTICATED) {
                            email = ""
                            password = ""
                            onLoginSuccess(role)
                        } else {
                            println("Error: Credenciales incorrectas.")

                        }
                    } catch (e: Exception) {
                        println("FATAL ERROR ROOM LOGIN: ${e.message}")
                    }
                }
            },
            // <<< VALIDACIÓN DE 6 CARACTERES Y EMAIL NO VACÍO >>>
            enabled = email.isNotBlank() && password.length >= 6,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para Crear Cuenta (Cliente)
        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Crear Cliente")
        }
    }
}