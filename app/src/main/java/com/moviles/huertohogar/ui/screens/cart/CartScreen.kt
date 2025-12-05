// Archivo: app/src/main/java/com.moviles.huertohogar/ui/screens/cart/CartScreen.kt

package com.moviles.huertohogar.ui.screens.cart

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.moviles.huertohogar.data.dao.OrderEntity
import com.moviles.huertohogar.data.database.AppDatabase
import com.moviles.huertohogar.domain.models.CartItem
import com.moviles.huertohogar.domain.models.ShoppingCart
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

@Composable
fun CartScreen(userEmail: String, onPaymentSuccess: () -> Unit) {
    val cartItems = ShoppingCart.items
    val totalPrice = ShoppingCart.getTotalPrice()
    val context = LocalContext.current

    // Obtenemos la Base de Datos completa para acceder a ambos DAOs
    val db = remember { AppDatabase.getDatabase(context) }
    val orderDao = remember { db.orderDao() }
    val productDao = remember { db.productDao() } // Necesario para gestionar el stock

    val scope = rememberCoroutineScope()

    var showPaymentForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Mi Carrito 🛒",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (cartItems.isEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Tu carrito está vacío.",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally))
            return
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cartItems) { item ->
                CartItemRow(item = item)
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total:", style = MaterialTheme.typography.titleLarge)
            Text(
                text = formatCurrency(totalPrice),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showPaymentForm = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
        ) {
            Text("Proceder al Pago")
        }
    }

    if (showPaymentForm) {
        CheckoutForm(
            onDismiss = { showPaymentForm = false },
            onConfirm = { name, address, date ->
                scope.launch {
                    try {
                        // --- 1. VALIDACIÓN PREVIA DE STOCK (CRÍTICO) ---
                        var stockError: String? = null

                        // Verificamos cada ítem del carrito contra la Base de Datos REAL
                        for (item in ShoppingCart.items) {
                            val productInDb = productDao.getProductById(item.fruit.id)

                            if (productInDb == null) {
                                stockError = "El producto '${item.fruit.name}' ya no existe."
                                break
                            } else if (item.quantity > productInDb.stock) {
                                // BLOQUEO: Si pide más de lo que hay, cancelamos todo
                                stockError = "Stock insuficiente para '${item.fruit.name}'. Disponibles: ${productInDb.stock}, Pedidos: ${item.quantity}"
                                break
                            }
                        }

                        if (stockError != null) {
                            // Si hubo error de stock, mostramos mensaje y NO procesamos la compra
                            Toast.makeText(context, stockError, Toast.LENGTH_LONG).show()
                            showPaymentForm = false
                            return@launch
                        }
                        // ------------------------------------------------

                        // --- 2. DESCUENTO DE STOCK Y GUARDADO ---
                        // Si llegamos aquí, el stock está confirmado. Procedemos.

                        ShoppingCart.items.forEach { cartItem ->
                            val productInDb = productDao.getProductById(cartItem.fruit.id)
                            if (productInDb != null) {
                                // Calculamos nuevo stock (asegurando que no sea negativo)
                                val newStock = (productInDb.stock - cartItem.quantity).coerceAtLeast(0)
                                // Actualizamos la DB
                                productDao.updateProduct(productInDb.copy(stock = newStock))
                            }
                        }

                        // Crear Resumen y Entidad de Pedido
                        val summary = ShoppingCart.items.joinToString(", ") { "${it.quantity}x ${it.fruit.name}" }
                        val newOrder = OrderEntity(
                            userEmail = userEmail,
                            clientName = name,
                            address = address,
                            date = date,
                            itemsSummary = summary,
                            total = ShoppingCart.getTotalPrice()
                        )
                        orderDao.insertOrder(newOrder)

                        // 3. LIMPIEZA Y SALIDA
                        ShoppingCart.clearCart()
                        showPaymentForm = false
                        onPaymentSuccess() // Volver a Productos
                        Toast.makeText(context, "¡Compra exitosa! Stock actualizado.", Toast.LENGTH_LONG).show()

                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }
}

@Composable
fun CartItemRow(item: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(3f)) {
            Text(item.fruit.name, style = MaterialTheme.typography.titleMedium)
            Text("${item.quantity} x ${formatCurrency(item.fruit.price)}",
                style = MaterialTheme.typography.bodySmall)
        }

        Text(
            text = formatCurrency(item.fruit.price * item.quantity),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        IconButton(onClick = { ShoppingCart.removeItem(item) }) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun CheckoutForm(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var clientName by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf("") }

    val isFormComplete = clientName.isNotBlank() && deliveryAddress.isNotBlank() && paymentMethod.isNotBlank() && deliveryDate.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Detalles de Despacho", style = MaterialTheme.typography.headlineSmall)
                    IconButton(onClick = onDismiss) { Icon(Icons.Filled.Close, contentDescription = "Cerrar") }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(value = clientName, onValueChange = { clientName = it }, label = { Text("Nombre Cliente") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = deliveryAddress, onValueChange = { deliveryAddress = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = paymentMethod, onValueChange = { paymentMethod = it }, label = { Text("Método de Pago") }, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
                OutlinedTextField(value = deliveryDate, onValueChange = { deliveryDate = it }, label = { Text("Fecha Despacho") }, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp))

                Button(
                    onClick = { onConfirm(clientName, deliveryAddress, deliveryDate) },
                    enabled = isFormComplete,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Confirmar Compra")
                }
            }
        }
    }
}