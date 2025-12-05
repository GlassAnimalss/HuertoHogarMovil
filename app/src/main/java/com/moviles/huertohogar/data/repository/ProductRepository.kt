package com.moviles.huertohogar.data.repository

import com.moviles.huertohogar.R
import com.moviles.huertohogar.data.dao.ProductDao
import com.moviles.huertohogar.data.dao.ProductEntity
import com.moviles.huertohogar.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val productDao: ProductDao) {

    private val apiService = RetrofitClient.apiService

    suspend fun refreshProductsFromApi() {
        withContext(Dispatchers.IO) {
            try {
                println("INTENTO: Conectando a la API...")
                val apiProducts = apiService.getHuertoProducts()

                // LÓGICA DE FUSIÓN INTELIGENTE (Smart Merge)
                apiProducts.forEach { dto ->
                    // 1. Buscamos si el producto ya existe en nuestra base de datos
                    val localProduct = productDao.getProductById(dto.id)

                    // 2. Si existe, usamos NUESTRO stock (el que editó el admin).
                    //    Si no existe, usamos el stock que viene de la API.
                    val stockToUse = localProduct?.stock ?: dto.stock

                    val entity = ProductEntity(
                        id = dto.id,
                        name = dto.nombre,
                        price = dto.precio.toDouble(),
                        unit = "unidad",
                        // CRÍTICO: Aquí guardamos el stock persistente
                        stock = stockToUse,
                        imageResId = R.drawable.huerto_hogar_2
                    )

                    productDao.insertProduct(entity)
                }

                println("ÉXITO: Productos sincronizados. Stock local preservado.")

            } catch (e: Exception) {
                println("ERROR API: ${e.message}.")
                e.printStackTrace()

                // FALLBACK: Solo cargamos datos de prueba si la base de datos está VACÍA.
                if (productDao.getProductCount() == 0) {
                    val mockProducts = listOf(
                        ProductEntity(101, "Manzana Roja", 1500.0, "kg", 50, R.drawable.manzanas_rojas),
                        ProductEntity(102, "Plátano", 990.0, "kg", 120, R.drawable.platano),
                        ProductEntity(103, "Naranja", 1200.0, "kg", 80, R.drawable.naranja),
                        ProductEntity(104, "Palta Hass", 3000.0, "unidad", 30, R.drawable.palta),
                        ProductEntity(105, "Sandía", 4500.0, "unidad", 15, R.drawable.sandia)
                    )
                    mockProducts.forEach { productDao.insertProduct(it) }
                }
            }
        }
    }
}