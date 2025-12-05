package com.moviles.huertohogar.data.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moviles.huertohogar.domain.auth.UserRole // Importamos el Enum que acabas de revisar

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val passwordHash: String, // Aquí se almacena la contraseña (simulada)
    val role: UserRole,
    val name: String? = null,
    val profileImageUri: String? = null
)