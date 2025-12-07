// Archivo: app/src/main/java/com.moviles.huertohogar/ui/screens/auth/LoginScreen.kt

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
import com.moviles.huertohogar.domain.auth.UserRole
import com.moviles.huertohogar.domain.utils.ValidationUtils // Importamos nuestra utilidad de validación
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole, String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // LOGO
        Image(
            painter = painterResource(id = R.drawable.huerto_hogar_2),
            contentDescription = "Logo HuertoHogar Login",
            modifier = Modifier
                .height(200.dp)
                .padding(bottom = 24.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "INICIO DE SESIÓN",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // CAMPO EMAIL (Con validación visual)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            // Se pone rojo si escribiste algo pero no es válido según tus Utils
            isError = email.isNotEmpty() && !ValidationUtils.isValidEmail(email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // CAMPO PASSWORD (Con validación visual)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = password.isNotEmpty() && !ValidationUtils.isValidPassword(password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )

        // BOTÓN
        Button(
            onClick = {
                scope.launch {
                    try {
                        val role = authRepository.loginUser(email, password)

                        if (role != UserRole.UNAUTHENTICATED) {
                            onLoginSuccess(role, email)
                            password = ""
                        } else {
                            Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        println("FATAL ERROR LOGIN: ${e.message}")
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            // USAMOS LA LÓGICA CENTRALIZADA DE VALIDACIÓN
            enabled = ValidationUtils.isValidEmail(email) && ValidationUtils.isValidPassword(password),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("¿No tienes cuenta? Crear Cliente")
        }
    }
}