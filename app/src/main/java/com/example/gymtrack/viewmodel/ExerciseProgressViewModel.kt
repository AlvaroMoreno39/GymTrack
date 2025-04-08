package com.example.gymtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ExerciseProgressViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _exerciseNames = MutableStateFlow<List<String>>(emptyList())
    val exerciseNames: StateFlow<List<String>> = _exerciseNames

    private val _weightBySession = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val weightBySession: StateFlow<List<Pair<String, Float>>> = _weightBySession

    fun loadExerciseNames(userId: String) {
        viewModelScope.launch {
            db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val nombres = mutableSetOf<String>()
                    for (doc in result) {
                        val rutina = doc.toObject(RoutineData::class.java)
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
                            if (ejercicio.nombre == exerciseName && ejercicio.peso > 0) {
                                progreso.add(fecha to ejercicio.peso.toFloat())
                            }
                        }
                    }

                    _weightBySession.value = progreso.sortedBy { it.first }
                }
        }
    }

    private val _allExercisesProgress = MutableStateFlow<Map<String, List<Pair<String, Float>>>>(emptyMap())
    val allExercisesProgress: StateFlow<Map<String, List<Pair<String, Float>>>> = _allExercisesProgress

    fun loadAllExerciseProgress(userId: String) {
        viewModelScope.launch {
            db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val data = mutableMapOf<String, MutableList<Pair<String, Float>>>()
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                    for (doc in result) {
                        val rutina = doc.toObject(com.example.gymtrack.viewmodel.RoutineData::class.java)
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