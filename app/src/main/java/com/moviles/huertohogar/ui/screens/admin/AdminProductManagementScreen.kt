// Archivo: ui/screens/admin/AdminProductManagementScreen.kt

package com.moviles.huertohogar.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.moviles.huertohogar.data.dao.ProductEntity
import com.moviles.huertohogar.data.dao.ProductDao
import com.moviles.huertohogar.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.rememberCoroutineScope
import java.text.NumberFormat
import java.util.Locale
val initialProducts = listOf(
    ProductEntity(1, "Manzana Roja", 1500.0, "kg", 50, com.moviles.huertohogar.R.drawable.manzanas_rojas),
    ProductEntity(2, "Plátano", 990.0, "kg", 120, com.moviles.huertohogar.R.drawable.platano),
    ProductEntity(3, "Naranja", 1200.0, "kg", 80, com.moviles.huertohogar.R.drawable.naranja),
    ProductEntity(4, "Palta Hass", 3000.0, "unidad", 30, com.moviles.huertohogar.R.drawable.palta),
    ProductEntity(5, "Uvas Crimson", 2500.0, "kg", 65, com.moviles.huertohogar.R.drawable.uvas_rojas),
    ProductEntity(6, "Sandía", 4500.0, "unidad", 15, com.moviles.huertohogar.R.drawable.sandia)
)

// ----------------------------------------------------
// PANTALLA PRINCIPAL DE ADMINISTRACIÓN
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductManagementScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val productDao = remember { AppDatabase.getDatabase(context).productDao() }

    // Observamos todos los productos desde Room
    val products by productDao.getAllProducts().collectAsState(initial = emptyList())

    // Inicialización de productos de ejemplo (solo la primera vez)
    LaunchedEffect(Unit) {
        scope.launch {
            if (products.isEmpty()) {
                initialProducts.forEach { productDao.insertProduct(it) }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar( // Usamos CenterAlignedTopAppBar para compatibilidad M3
                title = { Text("Panel de Administración") },
                actions = {
                    // Botón de Cerrar Sesión
                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Salir")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text(
                text = "Edición de Stock y Precios",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(products) { product ->
                    AdminProductItem(product = product, productDao = productDao, scope = scope)
                }
            }
        }
    }
}

// ----------------------------------------------------
// COMPONENTE: ITEM DE PRODUCTO EDITABLE
// ----------------------------------------------------

@Composable
fun AdminProductItem(product: ProductEntity, productDao: ProductDao, scope: CoroutineScope) {

    var priceText by remember { mutableStateOf(product.price.toString()) }
    var stockText by remember { mutableStateOf(product.stock.toString()) }

    // Formato CLP para la etiqueta
    val format = remember {
        NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply { maximumFractionDigits = 0 }
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text("ID: ${product.id} | Unidad: ${product.unit}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Campo de Edición de PRECIO
            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Precio Venta (${format.currency?.symbol})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // Campo de Edición de STOCK
            OutlinedTextField(
                value = stockText,
                onValueChange = { stockText = it },
                label = { Text("Stock Actual (Unidades)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            // Botón de GUARDAR CAMBIOS
            Button(
                onClick = {
                    val newPrice = priceText.toDoubleOrNull()
                    val newStock = stockText.toIntOrNull()

                    // Solo guardamos si ambos campos tienen valores válidos
                    if (newPrice != null && newStock != null) {
                        scope.launch {
                            productDao.updateProduct(
                                // Creamos una COPIA con los valores actualizados
                                product.copy(
                                    price = newPrice,
                                    stock = newStock
                                )
                            )
                            println("Producto ${product.name} actualizado por Admin.")
                        }
                    }
                },

                enabled = priceText.toDoubleOrNull() != product.price || stockText.toIntOrNull() != product.stock,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cambios")
            }
        }
    }
}