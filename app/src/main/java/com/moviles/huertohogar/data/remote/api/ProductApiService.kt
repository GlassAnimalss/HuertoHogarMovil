// Archivo: app/src/main/java/com.moviles.huertohogar/data/remote/api/ProductApiService.kt

package com.moviles.huertohogar.data.remote.api

import com.moviles.huertohogar.data.remote.dto.ProductDto
import com.moviles.huertohogar.data.remote.dto.CategoryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {

    // --- Endpoint Original ---
    @GET("api/huerto/productos")
    suspend fun getHuertoProducts(): List<ProductDto>




    @GET("api/productos")
    suspend fun getAllProducts(): List<ProductDto>


    @GET("api/productos")
    suspend fun searchProducts(@Query("buscar") query: String): List<ProductDto>


    @GET("api/productos/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto


    @GET("api/categorias")
    suspend fun getCategories(): List<CategoryDto>


    @GET("api/productos")
    suspend fun getProductsByCategory(@Query("categoria_id") categoryId: Int): List<ProductDto>
}