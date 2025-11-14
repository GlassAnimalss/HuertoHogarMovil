package com.moviles.huertohogar.ui.screens.stores

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Modelo de datos simple para una Sucursal
data class StoreLocation(
    val name: String,
    val address: String,
    val schedule: String
)

// Datos de ejemplo
private val sampleStores = listOf(
    StoreLocation(
        name = "Sucursal Centro",
        address = "Calle de la Huerta 456, Santiago",
        schedule = "Lun - Vie: 9:00 - 18:00"
    ),
    StoreLocation(
        name = "Sucursal Providencia",
        address = "Av. Jardín Alto 123, Providencia",
        schedule = "Mar - Sáb: 10:00 - 19:00"
    ),
    StoreLocation(
        name = "Sucursal Las Condes",
        address = "Pasaje Frutal 789, Las Condes",
        schedule = "Lun - Dom: 10:00 - 20:00"
    ),
    StoreLocation(
        name = "Sucursal Maipú",
        address = "Camino al Agricultor 101, Maipú",
        schedule = "Lun - Vie: 8:00 - 17:00"
    )
)

@Composable
fun StoresScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Nuestras Sucursales 🗺️",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleStores) { store ->
                StoreItem(store = store)
            }
        }
    }
}

@Composable
fun StoreItem(store: StoreLocation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nombre de la sucursal (usando color primario)
            Text(
                text = store.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Dirección
            Text(
                text = store.address,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 2.dp)
            )

            // Horario
            Text(
                text = "Horario: ${store.schedule}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}