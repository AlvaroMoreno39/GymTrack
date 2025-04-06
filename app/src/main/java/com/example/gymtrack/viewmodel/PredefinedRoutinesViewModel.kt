package com.example.gymtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Modelo de datos para representar una rutina
data class Routine(
    val name: String = "",
    val group: String = "",
    val type: String = "",
    val series: Int = 0,
    val reps: Int = 0,
    val duration: Int = 0,
    val intensity: String = ""
)

class PredefinedRoutinesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // Estado para almacenar la lista de rutinas
    private val _routines = MutableStateFlow<List<Routine>>(emptyList())
    val routines: StateFlow<List<Routine>> get() = _routines

    init {
        fetchRoutines()
    }

    // Obtener todas las rutinas de la colección rutinasPredefinidas
    private fun fetchRoutines() {
        viewModelScope.launch {
            db.collection("rutinasPredefinidas")
                .get()
                .addOnSuccessListener { result ->
                    val loadedRoutines = result.mapNotNull { doc ->
                        doc.toObject(Routine::class.java)
                    }
                    _routines.value = loadedRoutines
                }
                .addOnFailureListener {
                    println("❌ Error al cargar rutinas predefinidas: ${it.message}")
                }
        }
    }
}