// Archivo: app/src/main/java/com.moviles.huertohogar/ui/screens/auth/RegistrationScreen.kt

package com.moviles.huertohogar.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.moviles.huertohogar.R
import com.moviles.huertohogar.data.database.AppDatabase
import com.moviles.huertohogar.data.repository.AuthRepository
import com.moviles.huertohogar.domain.utils.ValidationUtils // Importamos la utilidad de validación
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    onRegistrationSuccess: () -> Unit, // Navegación de vuelta al login
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val authRepository = remember {
        AuthRepository(AppDatabase.getDatabase(context).userDao())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ---------------------------------------------
        // LOGO (Para mantener consistencia con Login)
        // ---------------------------------------------
        Image(
            painter = painterResource(id = R.drawable.huerto_hogar_2),
            contentDescription = "Logo HuertoHogar Registro",
            modifier = Modifier
                .height(200.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "CREAR CUENTA CLIENTE",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // CAMPO NOMBRE
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre Completo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // CAMPO EMAIL (Con validación visual)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            // Se pone rojo si no es válido
            isError = email.isNotEmpty() && !ValidationUtils.isValidEmail(email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // CAMPO PASSWORD (Con validación visual)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña (mín. 6 caracteres)") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            // Se pone rojo si no es válida
            isError = password.isNotEmpty() && !ValidationUtils.isValidPassword(password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )

        // BOTÓN DE REGISTRO
        Button(
            onClick = {
                scope.launch {
                    try {
                        val success = authRepository.registerUser(name, email, password)
                        if (success) {
                            // Limpiamos y notificamos éxito
                            name = ""
                            email = ""
                            password = ""
                            Toast.makeText(context, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                            onRegistrationSuccess()
                        } else {
                            // Error de negocio (ej. email duplicado)
                            Toast.makeText(context, "Error: El correo ya está registrado.", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        // Error técnico (Crash de Room)
                        println("FATAL ERROR REGISTER: ${e.message}")
                        Toast.makeText(context, "Error interno de base de datos", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            // Habilitado solo si TODO es válido según ValidationUtils
            enabled = name.isNotBlank() && ValidationUtils.isValidEmail(email) && ValidationUtils.isValidPassword(password),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Crear Cuenta")
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Volver al Inicio de Sesión")
        }
    }
}