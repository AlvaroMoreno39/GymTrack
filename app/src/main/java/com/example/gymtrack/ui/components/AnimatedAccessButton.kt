package com.example.gymtrack.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Botón animado reutilizable para acciones principales.
 * Al pulsar, invierte colores con animación y ejecuta la acción tras un breve retardo.
 */
@Composable
fun AnimatedAccessButton(
    buttonText: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    height: Dp = 56.dp,
    fontSize: TextUnit = 16.sp,
    border: BorderStroke? = BorderStroke(1.dp, color),
    cornerRadius: Dp = 8.dp,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }

    val backgroundColor by animateColorAsState(
        targetValue = if (pressed) contentColor else color,
        animationSpec = tween(durationMillis = 350),
        label = "AnimatedButtonBackground"
    )

    val animatedContentColor by animateColorAsState(
        targetValue = if (pressed) color else contentColor,
        animationSpec = tween(durationMillis = 350),
        label = "AnimatedButtonContent"
    )

    LaunchedEffect(pressed) {
        if (pressed) {
            delay(200)
            pressed = false
            onClick()
        }
    }

    Button(
        onClick = { pressed = true },
        modifier = modifier
            .height(height)
            .defaultMinSize(minWidth = 100.dp),
        shape = RoundedCornerShape(cornerRadius),
        border = border,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = animatedContentColor
        ),
        contentPadding = PaddingValues()
    ) {
        Text(buttonText, fontSize = fontSize)
    }
}