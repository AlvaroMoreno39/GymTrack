package com.alvaromoreno.gymtrack.ui.theme

import androidx.compose.ui.graphics.Color

// Colores base de la paleta personalizada de la app GymTrack.
// Estos colores se utilizan en combinación con los temas claro/oscuro definidos en Theme.kt.
// Al centralizarlos aquí, se facilita el mantenimiento del diseño visual.

// Blanco puro: se usa como fondo principal en modo claro y en textos sobre superficies oscuras
val PureWhite = Color(0xFFFFFFFF)

// Negro puro: se utiliza como color de texto o fondo principal en modo claro
val PureBlack = Color(0xFF000000)

// Blanco suave: ideal como fondo de tarjetas, Snackbars u otros componentes en modo claro
val SoftWhite = Color(0xFFF5F5F5)

// Gris muy claro: usado como fondo para elementos destacados como botones redondos animados
val VeryLightGray = Color(0xFFF0F0F0)

// Gris claro del sistema: útil para bordes, divisores o indicadores secundarios
val LightGray = Color.LightGray

// Rojo personalizado para botones de eliminar o acciones destructivas (alternativa a error por defecto)
val DeleteRed = Color(0xFFD32F2F)

// Amarillo para destacar elementos marcados como favoritos (ej. icono de estrella)
val FavoriteYellow = Color(0xFFFFC107)

// Verde validación: utilizado para indicar que una contraseña cumple criterios de seguridad
val ValidGreen = Color(0xFF00C853)
