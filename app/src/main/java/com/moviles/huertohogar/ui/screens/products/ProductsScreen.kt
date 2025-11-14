// Archivo: app/src/main/java/com.moviles.huertohogar/ui/screens/products/ProductsScreen.kt

package com.moviles.huertohogar.ui.screens.products

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
import com.moviles.huertohogar.data.dao.ProductEntity
import com.moviles.huertohogar.domain.models.Fruit
import com.moviles.huertohogar.domain.models.ShoppingCart
import java.text.NumberFormat
import java.util.Locale

// ----------------------------------------------------
// FUNCIÓN DE UTILIDAD
// ----------------------------------------------------

// Formato de moneda en Pesos Chilenos (CLP)
fun formatChileanPeso(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    format.maximumFractionDigits = 0 // Quita los centavos
    return format.format(amount)
}

// ----------------------------------------------------
// PANTALLA PRINCIPAL DE PRODUCTOS
// ----------------------------------------------------

@Composable
fun ProductsScreen() {
    val context = LocalContext.current
    // Obtenemos el DAO para acceder a los productos
    val productDao = remember { AppDatabase.getDatabase(context).productDao() }

    // Observamos todos los productos de la base de datos
    val productEntities by productDao.getAllProducts().collectAsState(initial = emptyList())

    // Mapeamos ProductEntity (de Room) a Fruit (para el ShoppingCart)
    val availableProducts = productEntities
        .filter { it.stock > 0 } // Filtramos para mostrar SOLO productos con Stock > 0
        .map {
            Fruit(it.id, it.name, it.price, it.unit, it.imageResId)
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

        // Listamos solo los productos que están disponibles (stock > 0)
        items(availableProducts) { fruit ->
            ProductItem(fruit = fruit)
        }
    }
}

// ----------------------------------------------------
// ITEM DE PRODUCTO INDIVIDUAL
// ----------------------------------------------------

@Composable
fun ProductItem(fruit: Fruit) {
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
            // Sección de Imagen
            Image(
                // Usa el ID del recurso guardado en la Entidad
                painter = painterResource(id = fruit.imageResId),
                contentDescription = fruit.name,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )

            // Sección de Texto e Info
            Column(modifier = Modifier.weight(1f)) {
                Text(text = fruit.name, style = MaterialTheme.typography.titleMedium)

                // Mostrar precio en CLP
                Text(
                    text = "${formatChileanPeso(fruit.price)} / ${fruit.unit}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Botón Añadir
            Button(onClick = {
                // Lógica para añadir al carrito: Se actualiza el estado global
                ShoppingCart.addItem(fruit)
            }) {
                Text("Añadir")
            }
        }
    }
}