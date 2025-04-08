package com.example.gymtrack.ui.screens

import android.graphics.Color
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gymtrack.viewmodel.ExerciseProgressViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExerciseProgressDashboardScreen(
    userId: String,
    viewModel: ExerciseProgressViewModel = viewModel()
) {
    val allProgress by viewModel.allExercisesProgress.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllExerciseProgress(userId)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "Evolución de carga por ejercicio",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (allProgress.isEmpty()) {
            Text("Cargando datos...")
        } else {
            allProgress.forEach { (nombre, lista) ->
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                AndroidView(
                    factory = { context ->
                        LineChart(context).apply {
                            val entries = lista.mapIndexed { index, par ->
                                Entry(index.toFloat(), par.second)
                            }

                            val labels = lista.map { it.first }

                            val dataSet = LineDataSet(entries, "Peso (kg)").apply {
                                color = Color.BLUE
                                valueTextSize = 12f
                                setCircleColor(Color.BLUE)
                                lineWidth = 2f
                                mode = LineDataSet.Mode.CUBIC_BEZIER
                            }

                            this.data = LineData(dataSet)

                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                valueFormatter = IndexAxisValueFormatter(labels)
                                granularity = 1f
                                textSize = 10f
                                labelRotationAngle = -25f
                            }

                            axisLeft.axisMinimum = 0f
                            axisRight.isEnabled = false
                            description.isEnabled = false
                            legend.isEnabled = false
                            animateX(1000)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}
