package com.example.gymtrack.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.gymtrack.viewmodel.GeneralProgressViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


@Composable
fun GeneralProgressScreen(
    userId: String,
    viewModel: GeneralProgressViewModel = GeneralProgressViewModel()
) {
    val datos by viewModel.datosPorGrupoMuscular.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.cargarDatos(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Progreso general del usuario",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (datos.isNotEmpty()) {
            AndroidView(
                factory = { context ->
                    BarChart(context).apply {
                        val grupos = datos.keys.toList()
                        val valores = datos.values.toList()

                        val entries = valores.mapIndexed { index, valor ->
                            BarEntry(index.toFloat(), valor)
                        }

                        val dataSet = BarDataSet(entries, "Series por grupo muscular").apply {
                            colors = listOf(
                                Color.rgb(255, 99, 132),   // Tríceps
                                Color.rgb(54, 162, 235),   // Piernas
                                Color.rgb(255, 206, 86),   // Espalda
                                Color.rgb(75, 192, 192),   // Bíceps
                                Color.rgb(153, 102, 255)   // Pecho
                            )

                            // 🔷 Estética visual
                            valueTextSize = 16f
                            valueTextColor = Color.BLACK
                            barBorderWidth = 1f
                            barShadowColor = Color.LTGRAY
                            highLightAlpha = 0
                            setDrawValues(true)
                        }

                        this.data = BarData(dataSet).apply {
                            barWidth = 0.5f
                        }

                        // 🧭 Eje X
                        xAxis.apply {
                            position = XAxis.XAxisPosition.BOTTOM
                            valueFormatter = IndexAxisValueFormatter(grupos)
                            granularity = 1f
                            setDrawGridLines(false)
                            textSize = 14f
                            labelRotationAngle = -15f
                        }

                        // 📏 Ejes Y
                        axisLeft.apply {
                            axisMinimum = 0f
                            textSize = 14f
                            setDrawGridLines(true)
                            gridColor = Color.LTGRAY
                        }

                        axisRight.isEnabled = false

                        // 🧼 Limpieza visual
                        description.isEnabled = false
                        legend.isEnabled = false

                        // ✨ Animación
                        animateY(1000)
                        setExtraOffsets(10f, 10f, 10f, 20f)
                        invalidate()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(8.dp)
            )

        } else {
            Text("Cargando datos...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}