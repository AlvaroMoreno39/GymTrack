package com.alvaromoreno.gymtrack.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Componente Snackbar personalizado animado, para notificaciones visuales en la app.
 * Aparece/desaparece con animación y diseño profesional.
 */
@Composable
fun FancySnackbarHost(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopCenter
) {
    val snackbarData = snackbarHostState.currentSnackbarData
    var show by remember { mutableStateOf(false) }

    // Controla la aparición/desaparición automática de la Snackbar
    LaunchedEffect(snackbarData) {
        if (snackbarData != null) {
            show = true
            delay(3000)
            show = false
            delay(500)
            snackbarData.dismiss()
        }
    }

    AnimatedVisibility(
        visible = show,
        enter = slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(500)),
        exit = slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeOut(tween(500)),
        modifier = modifier
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        snackbarData?.let { data ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = alignment
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shadowElevation = 12.dp,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    Text(
                        text = data.visuals.message,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                    )
                }
            }
        }
    }
}
