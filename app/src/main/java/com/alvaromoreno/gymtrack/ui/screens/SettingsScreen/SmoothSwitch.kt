package com.alvaromoreno.gymtrack.ui.screens.SettingsScreen

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Switch personalizado animado que adapta colores a claro/oscuro.
 * Mantiene transiciones suaves y accesibles.
 */
@Composable
fun SmoothSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDark = isSystemInDarkTheme()

    val switchColors = if (isDark) {
        SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = Color.Black,
            uncheckedThumbColor = Color.LightGray,
            uncheckedTrackColor = Color.DarkGray
        )
    } else {
        SwitchDefaults.colors(
            checkedThumbColor = Color.Black,
            checkedTrackColor = Color.LightGray,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color.Gray
        )
    }

    val transition = updateTransition(targetState = checked, label = "SwitchTransition")
    val thumbOffset by transition.animateDp(
        label = "ThumbOffset",
        transitionSpec = { tween(durationMillis = 400) }
    ) { state -> if (state) 20.dp else 0.dp }

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        interactionSource = interactionSource,
        colors = switchColors
    )
}