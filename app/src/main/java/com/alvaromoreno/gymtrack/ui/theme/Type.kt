package com.alvaromoreno.gymtrack.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Definición inicial de los estilos tipográficos usando Material 3 (Jetpack Compose)
// Esta configuración se usa por defecto en toda la app si se aplica el tema global
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,        // Familia tipográfica por defecto del sistema
        fontWeight = FontWeight.Normal,         // Peso normal (400)
        fontSize = 16.sp,                       // Tamaño de letra grande para texto base
        lineHeight = 24.sp,                     // Altura de línea para facilitar la lectura
        letterSpacing = 0.5.sp                  // Espaciado entre letras
    )
    // Puedes definir otros estilos adicionales descomentando y adaptando los siguientes bloques:

    /*
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,        // Título grande, por ejemplo en pantallas o secciones principales
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,        // Etiquetas pequeñas o textos secundarios (botones, inputs, etc.)
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
