package com.moviles.huertohogar.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

import androidx.compose.material3.*
import androidx.compose.runtime.*


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moviles.huertohogar.R
// Modelo de datos para las Preguntas Frecuentes
data class FaqItem(val question: String, val answer: String)

private val sampleFaqs = listOf(
    FaqItem("¿Cuál es el tiempo de entrega?", "Nuestras entregas se realizan en un plazo de 24 a 48 horas después de confirmar su pedido."),
    FaqItem("¿Son todos sus productos orgánicos?", "La mayoría lo son, pero siempre indicamos claramente cuáles tienen certificación orgánica y cuáles son de producción local y sostenible."),
    FaqItem("¿Cómo funciona el carrito de compras?", "Simplemente navegue a la pestaña 'Productos', añada lo que desee y vea el resumen en el icono del carrito en la parte superior derecha."),
    FaqItem("¿Aceptan devoluciones?", "Sí, si el producto no cumple con nuestros estándares de frescura y calidad, contáctenos en 4 horas para un reemplazo o reembolso.")
)

@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- 1. SECCIÓN DE BIENVENIDA Y CABECERA
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Texto de Bienvenida (PRIMERO)
            Text(
                text = "¡Bienvenido a HuertoHogar!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                // Usamos fillMaxWidth() para asegurar que el centrado sea sobre todo el ancho
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Center // Centrado Horizontal
            )

            // Texto descriptivo secundario
            Text(
                text = "Del campo a tu hogar. Frescura y calidad garantizadas.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Imagen del Box (SEGUNDO)
            Image(
                painter = painterResource(id = R.drawable.huerto_hogar_2),
                contentDescription = "Caja de HuertoHogar",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Título de la sección FAQ
            Text(
                text = "¿Tienes Preguntas? ",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )
        }

        // --- 2. SECCIÓN DE PREGUNTAS FRECUENTES (FAQ) ---
        items(sampleFaqs) { faqItem ->
            FaqItemCard(item = faqItem)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
// ---------------------------------------------------
// Componente: Tarjeta Expansible para FAQ
// ----------------------------------------------------

@Composable
fun FaqItemCard(item: FaqItem) {
    // Estado para controlar si la tarjeta está expandida o colapsada
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }, // Al hacer clic, se invierte el estado
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pregunta
                Text(
                    text = item.question,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f) // Ocupa el espacio restante
                )

                // Icono de expansión
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Colapsar" else "Expandir",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Respuesta (Solo visible si 'expanded' es true)
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.answer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}