

package com.moviles.huertohogar.domain.auth

// Enum para definir los roles de usuario
enum class UserRole {
    CLIENT,
    ADMIN,
    UNAUTHENTICATED // Estado inicial o cuando no ha iniciado sesión
}

// Clase para mantener el estado de la sesión
data class AuthState(
    val isAuthenticated: Boolean = false,
    val role: UserRole = UserRole.UNAUTHENTICATED
)