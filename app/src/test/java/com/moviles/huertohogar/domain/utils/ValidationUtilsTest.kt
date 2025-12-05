package com.moviles.huertohogar.domain.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    // --- PRUEBAS DE EMAIL ---

    @Test
    fun `email vacio debe ser invalido`() {
        val resultado = ValidationUtils.isValidEmail("")
        assertFalse("El email vacío no debería ser válido", resultado)
    }

    @Test
    fun `email sin arroba debe ser invalido`() {
        val resultado = ValidationUtils.isValidEmail("usuario.com")
        assertFalse(resultado)
    }

    @Test
    fun `email correcto debe ser valido`() {
        val resultado = ValidationUtils.isValidEmail("cliente@huertohogar.cl")
        assertTrue(resultado)
    }

    // --- PRUEBAS DE CONTRASEÑA ---

    @Test
    fun `password de 5 caracteres debe ser invalida`() {
        val resultado = ValidationUtils.isValidPassword("12345")
        assertFalse(resultado)
    }

    @Test
    fun `password de 6 caracteres debe ser valida`() {
        // Justo en el límite
        val resultado = ValidationUtils.isValidPassword("123456")
        assertTrue(resultado)
    }

    // --- PRUEBA DE FORMULARIO COMPLETO ---

    @Test
    fun `formulario con nombre vacio es invalido`() {
        val resultado = ValidationUtils.isRegistrationFormValid(
            name = "",
            email = "test@test.com",
            pass = "123456"
        )
        assertFalse(resultado)
    }
}