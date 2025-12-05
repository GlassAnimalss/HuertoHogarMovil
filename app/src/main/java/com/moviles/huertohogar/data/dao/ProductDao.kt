// Archivo: app/src/main/java/com.moviles.huertohogar/data/dao/ProductDao.kt

package com.moviles.huertohogar.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // Inserta o reemplaza (Usado por el repositorio)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    // Actualiza (Usado por el Admin para cambiar stock/precio)
    @Update
    suspend fun updateProduct(product: ProductEntity)

    // Obtiene todos los productos (Para las pantallas)
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    // --- NUEVO: Busca un producto por ID (Vital para mantener el stock local) ---
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity?

    // --- NUEVO: Cuenta cuántos productos hay (Para el fallback) ---
    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}