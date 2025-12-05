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
import com.moviles.huertohogar.data.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.rememberCoroutineScope
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductManagementScreen(onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val productDao = remember { AppDatabase.getDatabase(context).productDao() }
    // Usamos el repositorio para sincronizar datos reales
    val productRepository = remember { ProductRepository(productDao) }

    val products by productDao.getAllProducts().collectAsState(initial = emptyList())

    // Al entrar al panel, refrescamos los datos para asegurar consistencia con el cliente
    LaunchedEffect(Unit) {
        productRepository.refreshProductsFromApi()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel de Administración") },
                actions = {
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
                text = "Gestión de Inventario",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(products) { product ->
                        AdminProductItem(product = product, productDao = productDao, scope = scope)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminProductItem(product: ProductEntity, productDao: ProductDao, scope: CoroutineScope) {
    // key asegura que si el producto cambia en BD, los campos de texto se actualicen
    key(product.id, product.price, product.stock) {
        var priceText by remember { mutableStateOf(product.price.toString()) }
        var stockText by remember { mutableStateOf(product.stock.toString()) }

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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) priceText = it },
                        label = { Text("Precio") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { if (it.all { char -> char.isDigit() }) stockText = it },
                        label = { Text("Stock") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val newPrice = priceText.toDoubleOrNull()
                        val newStock = stockText.toIntOrNull()

                        if (newPrice != null && newStock != null) {
                            scope.launch {
                                productDao.updateProduct(
                                    product.copy(price = newPrice, stock = newStock)
                                )
                            }
                        }
                    },
                    // Solo habilitar si hay cambios y los valores son válidos
                    enabled = (priceText != product.price.toString() || stockText != product.stock.toString())
                            && stockText.toIntOrNull() != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios")
                }
            }
        }
    }
}