package com.moviles.huertohogar.ui.screens.auth

import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    onRegistrationSuccess: () -> Unit, // Navegación de vuelta al login
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // --- Instancias para Room y Coroutines ---
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepository = remember {
        AuthRepository(AppDatabase.getDatabase(context).userDao())
    }
    // -----------------------------------------

    // 1. Lógica de Validación de Email
    val isEmailValid = remember(email) {
        // Validación simple: no vacío, contiene '@' y '.'
        email.isNotBlank() && email.contains("@") && email.contains(".")
    }

    // 2. Condición Final para habilitar el botón
    val isFormValid = name.isNotBlank() && isEmailValid && password.length >= 6

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CREAR CUENTA CLIENTE",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // ---------------------------------------------
        // <<< CAMPOS DE TEXTO >>>
        // ---------------------------------------------
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre Completo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

            isError = email.isNotEmpty() && !isEmailValid,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    if (password.isNotEmpty() && password.length < 6)
                        "Contraseña (mínimo 6 caracteres)"
                    else
                        "Contraseña"
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = password.isNotEmpty() && password.length < 6, // Muestra error si es muy corta
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )
        // ---------------------------------------------

        // Botón de Registro
        Button(
            onClick = {
                scope.launch {
                    try {
                        val success = authRepository.registerUser(name, email, password)
                        if (success) {
                            // Limpiamos los campos y volvemos al Login
                            name = ""
                            email = ""
                            password = ""
                            onRegistrationSuccess()
                        } else {
                            println("Error: Email ya en uso o problema de registro.")
                        }
                    } catch (e: Exception) {
                        println("FATAL ERROR ROOM REGISTER: ${e.message}")
                    }
                }
            },
            // Habilitado solo si el formulario es completamente válido
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Crear Cuenta")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para volver al Login
        TextButton(onClick = onNavigateBack) {
            Text("Volver al Inicio de Sesión")
        }
    }
}