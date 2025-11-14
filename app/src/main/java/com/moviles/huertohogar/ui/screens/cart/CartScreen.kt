// Archivo: app/src/main/java/com.moviles.huertohogar/ui/screens/cart/CartScreen.kt

package com.moviles.huertohogar.ui.screens.cart

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.moviles.huertohogar.domain.models.CartItem
import com.moviles.huertohogar.domain.models.ShoppingCart
import java.text.NumberFormat
import java.util.Locale

// ----------------------------------------------------
// FUNCIÓN DE UTILIDAD: Formato de Moneda CLP
// ----------------------------------------------------

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

// ----------------------------------------------------
// PANTALLA PRINCIPAL DEL CARRITO
// ----------------------------------------------------

@Composable
fun CartScreen(onPaymentSuccess: () -> Unit) {
    val cartItems = ShoppingCart.items
    val totalPrice = ShoppingCart.getTotalPrice()
    val context = LocalContext.current

    var showPaymentForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Mi Carrito 🛒",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Carrito Vacío
        if (cartItems.isEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Tu carrito está vacío. ¡Añade algunas frutas frescas!",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally))
            return
        }

        // Lista de Ítems del Carrito
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cartItems) { item ->
                CartItemRow(item = item)
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Total a Pagar
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total a Pagar:", style = MaterialTheme.typography.titleLarge)
            Text(
                text = formatCurrency(totalPrice),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón que MUESTRA EL FORMULARIO DE PAGO
        Button(
            onClick = { showPaymentForm = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Proceder al Pago")
        }
    }

    // ----------------------------------------------------
    // FORMULARIO DE PAGO (Ventana Emergente - Dialog)
    // ----------------------------------------------------

    if (showPaymentForm) {
        CheckoutForm(
            onDismiss = { showPaymentForm = false },
            onConfirm = {
                ShoppingCart.clearCart()
                showPaymentForm = false
                onPaymentSuccess()
                Toast.makeText(context, "¡Compra Confirmada! Pronto será despachada.", Toast.LENGTH_LONG).show()
            }
        )
    }
}

// ----------------------------------------------------
// COMPONENTE: ITEM INDIVIDUAL DEL CARRITO
// ----------------------------------------------------

@Composable
fun CartItemRow(item: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(3f)) {
            Text(item.fruit.name, style = MaterialTheme.typography.titleMedium)
            Text("${item.quantity} x ${formatCurrency(item.fruit.price)} / ${item.fruit.unit}",
                style = MaterialTheme.typography.bodySmall)
        }

        // Precio subtotal del ítem
        Text(
            text = formatCurrency(item.fruit.price * item.quantity),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        // Botón para eliminar/decrementar
        IconButton(onClick = {
            ShoppingCart.removeItem(item)
        }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Eliminar ítem",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

// ----------------------------------------------------
// COMPONENTE: FORMULARIO DE DESPACHO
// ----------------------------------------------------

@Composable
fun CheckoutForm(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    // Estados del formulario
    var clientName by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf("") }

    // Verificación simple
    val isFormComplete = clientName.isNotBlank() && deliveryAddress.isNotBlank() && paymentMethod.isNotBlank() && deliveryDate.isNotBlank()

    // Usamos Dialog para la ventana modal
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Detalles de Despacho", style = MaterialTheme.typography.headlineSmall)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Nombre Completo
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nombre Cliente") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                // 2. Dirección de Entrega
                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text("Dirección de Entrega") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                // 3. Método de Pago (Simulación de Dropdown)
                OutlinedTextField(
                    value = paymentMethod,
                    onValueChange = { paymentMethod = it },
                    label = { Text("Método de Pago") },
                    placeholder = { Text("Efectivo / Tarjeta") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                // 4. Fecha de Despacho
                OutlinedTextField(
                    value = deliveryDate,
                    onValueChange = { deliveryDate = it },
                    label = { Text("Fecha de Despacho") },
                    placeholder = { Text("DD/MM/AAAA") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                // Botón de Confirmación
                Button(
                    onClick = onConfirm,
                    enabled = isFormComplete,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Confirmar Compra y Despacho")
                }
            }
        }
    }
}