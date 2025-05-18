package com.example.gymtrack.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PureBlack,
    onPrimary = PureWhite,
    background = SoftWhite,
    onBackground = PureBlack,
    surface = PureWhite,
    onSurface = PureBlack,
    error = Color.Red,
    onError = PureWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    background = Color(0xFF1A1A1A),
    onBackground = PureWhite,
    surface = Color(0xFF3A3A3A),
    onSurface = PureWhite,
    error = Color.Red,
    onError = PureBlack
)

@Composable
fun GymTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val targetColors = if (darkTheme) DarkColorScheme else LightColorScheme
    val colorScheme = rememberUpdatedState(targetColors)

    MaterialTheme(
        colorScheme = colorScheme.value,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}

