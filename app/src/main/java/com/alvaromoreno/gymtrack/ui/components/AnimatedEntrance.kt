package com.alvaromoreno.gymtrack.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable

/**
 * Permite que un bloque de contenido aparezca con animación de entrada (fade + slide).
 * Ideal para cabeceras y pantallas de bienvenida.
 */
@Composable
fun AnimatedEntrance(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(500)
        )
    ) {
        content()
    }
}



