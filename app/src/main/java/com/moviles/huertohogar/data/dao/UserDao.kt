package com.moviles.huertohogar.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow // Necesario para operaciones reactivas
import com.moviles.huertohogar.data.dao.UserEntity

@Dao // Anotación para marcar la interfaz como un DAO
interface UserDao {

    // Función para insertar un nuevo usuario (usada en el registro)
    @Insert
    suspend fun insertUser(user: UserEntity)

    // Función para buscar un usuario por email (usada en el login)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Función de ejemplo para obtener todos los usuarios (útil para administración o debug)
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("UPDATE users SET profileImageUri = :imageUri WHERE email = :email")
    suspend fun updateUserProfileImage(email: String, imageUri: String)


    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserFlow(email: String): kotlinx.coroutines.flow.Flow<UserEntity?>
}