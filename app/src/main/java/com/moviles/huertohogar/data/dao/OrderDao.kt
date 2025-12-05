package com.moviles.huertohogar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert
    suspend fun insertOrder(order: OrderEntity)

    // Consulta filtrada por email para el perfil del usuario
    @Query("SELECT * FROM orders WHERE userEmail = :email ORDER BY id DESC")
    fun getOrdersByUser(email: String): Flow<List<OrderEntity>>

    // Consulta general (opcional, para admin si quisieras)
    @Query("SELECT * FROM orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>
}