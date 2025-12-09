// Archivo: app/src/main/java/com.moviles.huertohogar/data/dao/ProductDao.kt

package com.moviles.huertohogar.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)


    @Update
    suspend fun updateProduct(product: ProductEntity)


    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()


    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity?


    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int
}