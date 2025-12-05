package com.moviles.huertohogar.domain.utils

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        // Regla: No vacío, tiene @ y tiene punto
        return email.isNotBlank() && email.contains("@") && email.contains(".")
    }

    fun isValidPassword(password: String): Boolean {
        // Regla: Mínimo 6 caracteres
        return password.length >= 6
    }

    fun isRegistrationFormValid(name: String, email: String, pass: String): Boolean {
        return name.isNotBlank() && isValidEmail(email) && isValidPassword(pass)
    }
}