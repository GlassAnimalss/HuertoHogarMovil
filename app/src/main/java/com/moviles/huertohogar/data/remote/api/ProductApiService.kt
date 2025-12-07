// Archivo: app/src/main/java/com.moviles.huertohogar/data/remote/api/ProductApiService.kt

package com.moviles.huertohogar.data.remote.api

import com.moviles.huertohogar.data.remote.dto.ProductDto
import com.moviles.huertohogar.data.remote.dto.CategoryDto // Necesitaremos crear este DTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {

    // --- Endpoint Original ---
    @GET("api/huerto/productos")
    suspend fun getHuertoProducts(): List<ProductDto>

    // --- NUEVOS ENDPOINTS ---

    // 1. Listar TODOS los productos (Catálogo general)
    @GET("api/productos")
    suspend fun getAllProducts(): List<ProductDto>

    // 2. Buscar productos (Buscador)
    // Esto genera una URL tipo: .../api/productos?buscar=manzana
    @GET("api/productos")
    suspend fun searchProducts(@Query("buscar") query: String): List<ProductDto>

    // 3. Obtener un producto específico por ID
    // Esto reemplaza {id} en la URL: .../api/productos/5
    @GET("api/productos/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    // 4. Listar Categorías
    @GET("api/categorias")
    suspend fun getCategories(): List<CategoryDto>

    // 5. Filtrar productos por Categoría (Ejemplo adicional útil)
    @GET("api/productos")
    suspend fun getProductsByCategory(@Query("categoria_id") categoryId: Int): List<ProductDto>
}