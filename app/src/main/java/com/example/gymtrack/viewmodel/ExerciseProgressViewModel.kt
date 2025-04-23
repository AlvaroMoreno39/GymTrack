package com.example.gymtrack.viewmodel

/*
ExerciseProgressViewModel.kt

Este ViewModel gestiona toda la lógica relacionada con el progreso de los ejercicios del usuario.
Permite obtener:
- Los nombres únicos de los ejercicios que contienen peso.
- El progreso de un ejercicio concreto a lo largo del tiempo.
- El progreso de todos los ejercicios para mostrar estadísticas más completas.

Toda la información se obtiene de Firestore, de la colección `rutinas` del usuario actual.
Los datos están estructurados para ser usados directamente en gráficas con MPAndroidChart.
*/

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ExerciseProgressViewModel : ViewModel() {

    // Conexión a la base de datos de Firebase Firestore
    private val db = FirebaseFirestore.getInstance()

    // Lista de nombres de ejercicios con registros de peso
    private val _exerciseNames = MutableStateFlow<List<String>>(emptyList())
    val exerciseNames: StateFlow<List<String>> = _exerciseNames

    // Lista con pares (fecha, peso) para un ejercicio concreto
    private val _weightBySession = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val weightBySession: StateFlow<List<Pair<String, Float>>> = _weightBySession

    /**
     * Carga los nombres únicos de los ejercicios del usuario que tienen peso registrado.
     * Sirve para poblar el selector (spinner o menú) de ejercicios disponibles.
     */
    fun loadExerciseNames(userId: String) {
        viewModelScope.launch {
            db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val nombres = mutableSetOf<String>()
                    for (doc in result) {
                        val rutina = doc.toObject(RoutineData::class.java)
                        // Añade nombres si el ejercicio tiene peso
                        rutina.ejercicios.forEach { ejercicio ->
                            if (ejercicio.peso > 0 && ejercicio.nombre.isNotBlank()) {
                                nombres.add(ejercicio.nombre)
                            }
                        }
                    }
                    _exerciseNames.value = nombres.toList()
                }
        }
    }

    /**
     * Carga el progreso de un ejercicio específico a lo largo del tiempo.
     * Devuelve una lista con pares (fecha, peso) para construir una gráfica temporal.
     */
    fun loadProgressForExercise(userId: String, exerciseName: String) {
        viewModelScope.launch {
            db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val progreso = mutableListOf<Pair<String, Float>>()

                    for (doc in result) {
                        val rutina = doc.toObject(RoutineData::class.java)
                        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(rutina.fechaCreacion.toDate())

                        rutina.ejercicios.forEach { ejercicio ->
                            // Solo añade datos si el nombre coincide y hay peso
                            if (ejercicio.nombre == exerciseName && ejercicio.peso > 0) {
                                progreso.add(fecha to ejercicio.peso.toFloat())
                            }
                        }
                    }

                    // Ordena por fecha antes de mostrar
                    _weightBySession.value = progreso.sortedBy { it.first }
                }
        }
    }

    // Mapa con el progreso de todos los ejercicios (clave = nombre, valor = lista de pares fecha/peso)
    private val _allExercisesProgress = MutableStateFlow<Map<String, List<Pair<String, Float>>>>(emptyMap())
    val allExercisesProgress: StateFlow<Map<String, List<Pair<String, Float>>>> = _allExercisesProgress

    /**
     * Carga el progreso de todos los ejercicios del usuario actual.
     * Estructura los datos en un mapa para mostrar todas las gráficas a la vez si es necesario.
     */
    fun loadAllExerciseProgress(userId: String) {
        viewModelScope.launch {
            db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val data = mutableMapOf<String, MutableList<Pair<String, Float>>>()
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    for (doc in result) {
                        val rutina = doc.toObject(RoutineData::class.java)
                        val fecha = formatter.format(rutina.fechaCreacion.toDate())

                        rutina.ejercicios.forEach { ejercicio ->
                            if (ejercicio.peso > 0 && ejercicio.nombre.isNotBlank()) {
                                data.getOrPut(ejercicio.nombre) { mutableListOf() }
                                    .add(fecha to ejercicio.peso.toFloat())
                            }
                        }
                    }

                    _allExercisesProgress.value = data
                }
        }
    }

}