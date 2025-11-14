

package com.moviles.huertohogar.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // Inserta o reemplaza (útil para la inicialización y edición)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    // Actualiza el producto (usado por el Admin)
    @Update
    suspend fun updateProduct(product: ProductEntity)

    // Obtiene todos los productos para mostrar al cliente y al admin
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>
}