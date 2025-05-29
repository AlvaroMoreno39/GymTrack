package com.alvaromoreno.gymtrack.ui.screens.HomeScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alvaromoreno.gymtrack.ui.components.AnimatedAccessButton
import com.alvaromoreno.gymtrack.ui.theme.LightGray

/**
 * RutinasCard
 *
 * Componente reutilizable que muestra una opción de la Home en formato Card visual.
 * Incluye imagen, título, descripción y un botón animado de acceso.
 */
@Composable
fun RutinasCard(
    title: String,            // Título principal de la opción
    description: String,      // Descripción breve de la opción
    imageRes: Int,            // Recurso de imagen asociado a la opción
    onClick: () -> Unit       // Acción a ejecutar al pulsar el botón "Acceder"
) {
    Card(
        shape = RoundedCornerShape(16.dp),   // Esquinas redondeadas
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagen decorativa en la parte superior de la card
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )

            // Contenido textual y botón
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = LightGray,
                    textAlign = TextAlign.Center
                )

                // Botón de acceso animado, estilizado como el resto de la app
                AnimatedAccessButton(
                    buttonText = "Acceder",
                    color = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background,
                    fontSize = 15.sp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp),
                    onClick = onClick
                )
            }
        }
    }
}
