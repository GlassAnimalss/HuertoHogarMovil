// Archivo: app/src/main/java/com.moviles.huertohogar/data/repository/ProductRepository.kt

package com.moviles.huertohogar.data.repository

import com.moviles.huertohogar.R
import com.moviles.huertohogar.data.dao.ProductDao
import com.moviles.huertohogar.data.dao.ProductEntity
import com.moviles.huertohogar.data.remote.RetrofitClient
import com.moviles.huertohogar.data.remote.dto.CategoryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val productDao: ProductDao) {

    private val apiService = RetrofitClient.instance

    // --- MAPA DE IMÁGENES SEGURAS ---
    private fun getReliableImage(productName: String, originalUrl: String?): String? {
        val name = productName.lowercase()
        return when {
            "tomate" in name -> "https://images.unsplash.com/photo-1546094096-0df4bcaaa337?auto=format&fit=crop&w=400&q=80"
            "lechuga" in name -> "https://images.unsplash.com/photo-1622206151226-18ca2c9ab4a1?auto=format&fit=crop&w=400&q=80"
            "fresa" in name || "frutilla" in name -> "https://images.unsplash.com/photo-1587393855524-087f83d95bc9?auto=format&fit=crop&w=400&q=80"
            "manzana" in name -> "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&w=400&q=80"
            "albahaca" in name -> "https://images.unsplash.com/photo-1618160702438-9b02ab6515c9?auto=format&fit=crop&w=400&q=80"

            // NUEVA URL PARA CILANTRO (Más estable)
            "cilantro" in name -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRuuw0w717XeGrF2JU1dw04eU8wZmpm-9PxBw&s"

            "zanahoria" in name -> "https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?auto=format&fit=crop&w=400&q=80"
            "frijol" in name || "poroto" in name -> "https://images.unsplash.com/photo-1551462147-37885acc36f1?auto=format&fit=crop&w=400&q=80"
            "limon" in name -> "https://images.unsplash.com/photo-1590502593747-42a996133562?auto=format&fit=crop&w=400&q=80"
            else -> originalUrl
        }
    }

    suspend fun refreshProductsFromApi() {
        withContext(Dispatchers.IO) {
            try {
                println("INTENTO: Conectando a la API...")
                val apiProducts = apiService.getHuertoProducts()

                apiProducts.forEach { dto ->
                    val localProduct = productDao.getProductById(dto.id)
                    val stockToUse = localProduct?.stock ?: dto.stock
                    val priceDouble = dto.precio.toDoubleOrNull() ?: 0.0

                    val prettyImage = getReliableImage(dto.nombre, dto.imagenUrl)

                    val entity = ProductEntity(
                        id = dto.id,
                        name = dto.nombre,
                        price = priceDouble,
                        unit = dto.unidad ?: "unidad",
                        stock = stockToUse,
                        imageUrl = prettyImage
                    )

                    productDao.insertProduct(entity)
                }
                println("ÉXITO: ${apiProducts.size} productos cargados.")
            } catch (e: Exception) {
                println("ERROR API PRODUCTOS: ${e.message}")
                e.printStackTrace()

                // FALLBACK
                if (productDao.getProductCount() == 0) {
                    val mockProducts = listOf(
                        ProductEntity(101, "Manzana Roja (Local)", 1500.0, "kg", 50, "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&w=400&q=80"),
                        ProductEntity(102, "Plátano (Local)", 990.0, "kg", 120, "https://images.unsplash.com/photo-1528825871115-3581a5387919?auto=format&fit=crop&w=400&q=80"),
                        ProductEntity(103, "Naranja (Local)", 1200.0, "kg", 80, "https://images.unsplash.com/photo-1547514701-42782101795e?auto=format&fit=crop&w=400&q=80"),
                        ProductEntity(104, "Palta Hass (Local)", 3000.0, "unidad", 30, "https://plus.unsplash.com/premium_photo-1675731118330-08c7125bcfa1?q=80&w=200"),
                        ProductEntity(105, "Sandía (Local)", 4500.0, "unidad", 15, "https://images.unsplash.com/photo-1589593259466-237198a28723?auto=format&fit=crop&w=400&q=80")
                    )
                    mockProducts.forEach { productDao.insertProduct(it) }
                }
            }
        }
    }

    suspend fun getHuertoCategories(): List<CategoryDto> {
        return withContext(Dispatchers.IO) {
            try {
                val allCategories = apiService.getCategories()
                val huertoCategories = allCategories.filter {
                    it.tiendaSlug == "huerto"
                }
                return@withContext huertoCategories
            } catch (e: Exception) {
                return@withContext emptyList()
            }
        }
    }
}