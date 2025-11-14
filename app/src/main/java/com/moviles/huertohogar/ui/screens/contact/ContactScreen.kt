package com.moviles.huertohogar.ui.screens.contact

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
@Composable
fun ContactScreen() {
    // Para mostrar mensajes emergentes (Toast)
    val context = LocalContext.current

    // Estados para los campos del formulario
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Función de acción simple para el botón
    val onSendClick: () -> Unit = {
        if (name.isNotBlank() && email.isNotBlank() && message.isNotBlank()) {
            // Lógica de envío simulada
            Toast.makeText(
                context,
                "Mensaje enviado por $name. ¡Pronto te contactaremos!",
                Toast.LENGTH_LONG
            ).show()

            // Limpiar los campos después del envío
            name = ""
            email = ""
            message = ""
        } else {
            Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Contáctanos",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Campo para Nombre
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tu Nombre") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Campo para Correo Electrónico
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Campo para Mensaje
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Tu Mensaje") },
            minLines = 5,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Botón de Envío
        Button(
            onClick = onSendClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Mensaje")
        }
    }
}