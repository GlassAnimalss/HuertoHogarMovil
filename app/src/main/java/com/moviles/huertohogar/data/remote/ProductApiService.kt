package com.moviles.huertohogar.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. La Interfaz: Define las "instrucciones" para pedir datos
interface ProductApiService {
    // Aquí le decimos: "Ve a /api/huerto y tráeme una lista de ProductDto"
    @GET("api/huerto")
    suspend fun getHuertoProducts(): List<ProductDto>
}

// 2. El Cliente: Configura la conexión real
object RetrofitClient {
    // La URL que te dio tu profesor
    private const val BASE_URL = "https://api-dfs2-dm-production.up.railway.app/"

    val apiService: ProductApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Esto convierte el JSON a tus objetos Kotlin automáticamente
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApiService::class.java)
    }
}