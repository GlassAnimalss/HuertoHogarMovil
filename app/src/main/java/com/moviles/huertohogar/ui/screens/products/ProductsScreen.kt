// Archivo: app/src/main/java/com.moviles.huertohogar/ui/screens/products/ProductsScreen.kt

package com.moviles.huertohogar.ui.screens.products

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // NECESARIO COIL
import com.moviles.huertohogar.R
import com.moviles.huertohogar.data.database.AppDatabase
import com.moviles.huertohogar.data.repository.ProductRepository
import com.moviles.huertohogar.domain.models.Fruit
import com.moviles.huertohogar.domain.models.ShoppingCart
import java.text.NumberFormat
import java.util.Locale

fun formatChileanPeso(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

@Composable
fun ProductsScreen() {
    val context = LocalContext.current
    val productDao = remember { AppDatabase.getDatabase(context).productDao() }
    val productRepository = remember { ProductRepository(productDao) }

    val productEntities by productDao.getAllProducts().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        productRepository.refreshProductsFromApi()
    }

    val availableProducts = productEntities.map {
        Fruit(it.id, it.name, it.price, it.unit, it.stock, it.imageUrl)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "🛒 Productos Frescos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(items = availableProducts, key = { it.id }) { fruit ->
            ProductItem(fruit = fruit)
        }
    }
}

@Composable
fun ProductItem(fruit: Fruit) {
    val context = LocalContext.current
    val quantityInCart by remember {
        derivedStateOf { ShoppingCart.items.find { it.fruit.id == fruit.id }?.quantity ?: 0 }
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

            // --- LÓGICA DE IMAGEN (URL vs LOCAL) ---
            if (fruit.imageUrl != null) {
                AsyncImage(
                    model = fruit.imageUrl,
                    contentDescription = fruit.name,
                    modifier = Modifier.size(64.dp).padding(end = 12.dp),
                    contentScale = ContentScale.Crop,
                    // Si la URL falla, muestra la caja por defecto
                    error = painterResource(id = R.drawable.huerto_hogar_2)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.huerto_hogar_2),
                    contentDescription = fruit.name,
                    modifier = Modifier.size(64.dp).padding(end = 12.dp),
                    contentScale = ContentScale.Crop
                )
            }
            // ---------------------------------------

            Column(modifier = Modifier.weight(1f)) {
                Text(text = fruit.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${formatChileanPeso(fruit.price)} / ${fruit.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Stock: ${fruit.stock}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (fruit.stock == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    if (quantityInCart < fruit.stock) {
                        ShoppingCart.addItem(fruit)
                    } else {
                        Toast.makeText(context, "¡Stock insuficiente!", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = fruit.stock > 0 && quantityInCart < fruit.stock
            ) {
                Text(if (fruit.stock == 0) "Agotado" else "Añadir")
            }
        }
    }
}