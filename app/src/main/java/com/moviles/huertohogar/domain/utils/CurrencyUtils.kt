package com.moviles.huertohogar.domain.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun formatChileanPeso(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }
}