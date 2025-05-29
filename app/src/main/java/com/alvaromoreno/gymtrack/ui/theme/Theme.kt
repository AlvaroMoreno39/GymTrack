package com.alvaromoreno.gymtrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color

// Definición del esquema de colores para el modo claro
private val LightColorScheme = lightColorScheme(
    primary = PureBlack,           // Color principal (botones, iconos, destacados)
    onPrimary = PureWhite,         // Color del contenido que se muestra sobre primary
    background = SoftWhite,        // Color del fondo principal de la app
    onBackground = PureBlack,      // Texto e iconos sobre el fondo
    surface = PureWhite,           // Color de las superficies (cards, menús, etc.)
    onSurface = PureBlack,         // Texto e iconos sobre superficies
    error = Color.Red,             // Color para errores (formularios, validaciones)
    onError = PureWhite            // Texto sobre fondo de error
)

// Definición del esquema de colores para el modo oscuro
private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,           // Color principal (cambia a blanco en modo oscuro)
    onPrimary = PureBlack,         // Texto sobre el color principal
    background = Color(0xFF1A1A1A),// Fondo oscuro personalizado
    onBackground = PureWhite,      // Texto sobre fondo oscuro
    surface = Color(0xFF3A3A3A),   // Color de tarjetas o menús en modo oscuro
    onSurface = PureWhite,         // Texto sobre superficies
    error = Color.Red,             // Error (mantiene el rojo por claridad)
    onError = PureBlack            // Texto sobre fondo de error (rojo)
)

@Composable
fun GymTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Por defecto sigue el tema del sistema
    content: @Composable () -> Unit             // Contenido a renderizar dentro del tema
) {
    // Determina cuál esquema de colores usar en función del modo oscuro/claro
    val targetColors = if (darkTheme) DarkColorScheme else LightColorScheme

    // Envuelve el esquema en un estado para que sea reactivo
    val colorScheme = rememberUpdatedState(targetColors)

    // Aplica el tema usando MaterialTheme de Jetpack Compose
    MaterialTheme(
        colorScheme = colorScheme.value,     // Esquema de colores actual
        typography = Typography(),           // Tipografía definida en Type.kt
        shapes = Shapes(),                   // Formas por defecto (esquinas redondeadas, etc.)
        content = content                    // Contenido visual que se dibuja con este tema
    )
}
