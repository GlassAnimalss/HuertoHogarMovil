package com.moviles.huertohogar.data.repository

import com.moviles.huertohogar.data.dao.UserDao
import com.moviles.huertohogar.data.dao.UserEntity
import com.moviles.huertohogar.domain.auth.UserRole

// Clase para manejar la lógica de la base de datos de usuarios
class AuthRepository(private val userDao: UserDao) {

    // Simulación de registro de un nuevo usuario cliente
    suspend fun registerUser(name: String, email: String, password: String): Boolean {

        val newUser = UserEntity(
            name = name,
            email = email,
            passwordHash = password,
            role = UserRole.CLIENT
        )
        return try {
            userDao.insertUser(newUser)
            true
        } catch (e: Exception) {
            // Manejar error (e.g., email duplicado)
            false
        }
    }

    // Función para crear el usuario ADMIN si no existe
    suspend fun createAdminIfNotExist() {
        if (userDao.getUserByEmail("admin@huertohogar.cl") == null) {
            val adminUser = UserEntity(
                name = "Admin HuertoHogar",
                email = "admin@huertohogar.cl",
                passwordHash = "admin123", // Credenciales de acceso del Admin
                role = UserRole.ADMIN
            )
            userDao.insertUser(adminUser)
        }
    }

    // Simulación de inicio de sesión
    suspend fun loginUser(email: String, password: String): UserRole {
        val user = userDao.getUserByEmail(email)

        // Comprobar si existe y si la "contraseña" coincide
        return if (user != null && user.passwordHash == password) {
            user.role
        } else {
            UserRole.UNAUTHENTICATED
        }
    }
}