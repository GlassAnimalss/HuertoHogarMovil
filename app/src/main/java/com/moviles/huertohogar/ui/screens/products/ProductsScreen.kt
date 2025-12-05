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
import com.moviles.huertohogar.R
import com.moviles.huertohogar.data.database.AppDatabase
import com.moviles.huertohogar.data.repository.ProductRepository
import com.moviles.huertohogar.domain.models.Fruit
import com.moviles.huertohogar.domain.models.ShoppingCart
import java.text.NumberFormat
import java.util.Locale

// ----------------------------------------------------
// FUNCIÓN DE UTILIDAD
// ----------------------------------------------------

fun formatChileanPeso(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

// ----------------------------------------------------
// PANTALLA PRINCIPAL DE PRODUCTOS
// ----------------------------------------------------

@Composable
fun ProductsScreen() {
    val context = LocalContext.current

    // 1. Inicialización de DAO y Repositorio
    val productDao = remember { AppDatabase.getDatabase(context).productDao() }
    val productRepository = remember { ProductRepository(productDao) }

    // 2. Observamos la base de datos (Fuente de Verdad)
    // Cualquier cambio en Room (por la API o por el Admin) se refleja aquí automáticamente
    val productEntities by productDao.getAllProducts().collectAsState(initial = emptyList())

    // 3. Sincronización con API al iniciar
    LaunchedEffect(Unit) {
        productRepository.refreshProductsFromApi()
    }

    // 4. Mapeo de Entidad (BD) a Modelo de Dominio (UI)
    val availableProducts = productEntities.map {
        Fruit(
            id = it.id,
            name = it.name,
            price = it.price,
            unit = it.unit,
            stock = it.stock,
            imageResId = it.imageResId
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🛒 Productos Frescos",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Usamos 'key' para mejorar el rendimiento de la lista
        items(
            items = availableProducts,
            key = { it.id }
        ) { fruit ->
            ProductItem(fruit = fruit)
        }
    }
}

// ----------------------------------------------------
// ITEM DE PRODUCTO INDIVIDUAL
// ----------------------------------------------------

@Composable
fun ProductItem(fruit: Fruit) {
    val context = LocalContext.current

    // Calculamos cuántas unidades de este producto ya están en el carrito
    val quantityInCart by remember {
        derivedStateOf {
            ShoppingCart.items.find { it.fruit.id == fruit.id }?.quantity ?: 0
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del Producto
            Image(
                painter = painterResource(id = fruit.imageResId),
                contentDescription = fruit.name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )

            // Información (Nombre, Precio, Stock)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = fruit.name, style = MaterialTheme.typography.titleMedium)

                Text(
                    text = "${formatChileanPeso(fruit.price)} / ${fruit.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )

                // Indicador visual de Stock
                Text(
                    text = "Stock: ${fruit.stock}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (fruit.stock == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Botón de Acción
            Button(
                onClick = {
                    // VALIDACIÓN: Solo permitir si la cantidad en carrito es menor al stock real
                    if (quantityInCart < fruit.stock) {
                        ShoppingCart.addItem(fruit)
                    } else {
                        Toast.makeText(context, "¡No hay más stock disponible! (Máx: ${fruit.stock})", Toast.LENGTH_SHORT).show()
                    }
                },
                // El botón se deshabilita visualmente si no hay stock
                enabled = fruit.stock > 0
            ) {
                Text(if (fruit.stock == 0) "Agotado" else "Añadir")
            }
        }
    }
}