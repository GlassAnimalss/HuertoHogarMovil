

package com.moviles.huertohogar.ui.common
import com.moviles.huertohogar.R
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moviles.huertohogar.domain.models.ShoppingCart
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
@Composable
fun AppHeader(onCartClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- LOGO DE HUERTOHOGAR ---
        Image(
            painter = painterResource(id = R.drawable.huerto_hogar_1),
            contentDescription = "Logo HuertoHogar",
            modifier = Modifier
                .height(64.dp)
                .widthIn(max = 180.dp),
            contentScale = ContentScale.Fit
        )

        // Carrito de Compras
        BadgedBox(
            badge = {

                val totalItems = ShoppingCart.getTotalItems()


                if (totalItems > 0) {
                    Badge(

                        containerColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Text(totalItems.toString())
                    }
                }
            },
            modifier = Modifier.padding(end = 8.dp)
        ) {

            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = "Carrito de Compras",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}