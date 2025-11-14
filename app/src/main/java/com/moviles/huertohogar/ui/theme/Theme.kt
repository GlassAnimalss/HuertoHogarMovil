

package com.moviles.huertohogar.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ----------------------------------------------------
// 1. PALETA DE COLORES CLARO (Orgánica HuertoHogar)
// ----------------------------------------------------

private val LightColorScheme = lightColorScheme(
    // Colores Primarios (Verde Pastoso)
    primary = PrimaryGreen,
    onPrimary = OnPrimaryWhite,

    // Colores Secundarios (Verde Pastel)
    secondary = SecondaryGreen,
    onSecondary = OnPrimaryWhite,

    // Colores Terciarios (Naranja Cosecha/Énfasis/Precio)
    tertiary = TertiaryOrange,
    onTertiary = OnTertiaryBlack,

    // Fondos y Superficies (Beige / Café Claro)
    background = BackgroundCream,
    onBackground = OnBackgroundDark,

    surface = SurfaceLight,
    onSurface = OnBackgroundDark,

    // Alerta (Error)
    error = ErrorRed,
    onError = OnPrimaryWhite
)

// ----------------------------------------------------
// 2. PALETA DE COLORES OSCURO (Ajuste simple)
// ----------------------------------------------------

private val DarkColorScheme = darkColorScheme(

    primary = SecondaryGreen,
    onPrimary = Color.Black,

    secondary = PrimaryGreen,
    onSecondary = Color.White,

    tertiary = TertiaryOrange,
    onTertiary = Color.Black,

    // Fondos oscuros estándar
    background = Color(0xFF121212),
    onBackground = Color.White,

    surface = Color(0xFF1D1D1D),
    onSurface = Color.White,

    error = ErrorRed
)

// ----------------------------------------------------
// 3. FUNCIÓN COMPOSABLE DEL TEMA
// ----------------------------------------------------

@Composable
fun HuertoHogarTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Mantener color dinámico desactivado para control total de la paleta
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // 1. Selección de Color Scheme
    val colorScheme = when {

        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // 2. Efecto Secundario para la Barra de Estado
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Establece el color de la barra de estado al color de fondo
            window.statusBarColor = colorScheme.background.toArgb()
            // Controla si los iconos de la barra de estado deben ser claros u oscuros
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // 3. Aplicación del MaterialTheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asume que la variable Typography está en Type.kt
        content = content
    )
}