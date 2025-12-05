// Archivo: app/src/main/java/com/moviles/huertohogar/data/remote/ProductApiService.kt

package com.moviles.huertohogar.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ProductApiService {
    @GET("api/huerto")
    suspend fun getHuertoProducts(): List<ProductDto>
}

object RetrofitClient {
    private const val BASE_URL = "https://api-dfs2-dm-production.up.railway.app/"

    // 1. Creamos el "Espía" (Interceptor)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        // LEVEL BODY es el más detallado: Muestra la URL, cabeceras y el JSON completo
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. Configuramos el Cliente HTTP para usar el espía
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    // 3. Conectamos Retrofit con nuestro cliente especial
    val apiService: ProductApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <--- AQUÍ ESTÁ LA CLAVE
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApiService::class.java)
    }
}