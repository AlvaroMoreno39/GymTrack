package com.example.gymtrack.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.gymtrack.viewmodel.PredefinedRoutinesViewModel
import com.example.gymtrack.viewmodel.RoutineData
import com.example.gymtrack.viewmodel.RoutineViewModel


// ViewModel para rutinas predefinidas
class PredefinedRoutinesViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // Lista de rutinas predefinidas con sus datos completos
    private val _routines = MutableStateFlow<List<RoutineData>>(emptyList())
    val routines: StateFlow<List<RoutineData>> get() = _routines

    // Cargar rutinas desde Firestore
    fun fetchRoutines(onResult: (List<RoutineData>) -> Unit) {
        db.collection("rutinasPredefinidas")
            .get()
            .addOnSuccessListener { result ->
                val routines = result.mapNotNull { doc ->
                    doc.toObject(RoutineData::class.java)
                }
                _routines.value = routines
                onResult(routines)
            }
            .addOnFailureListener {
                println("❌ Error al cargar rutinas predefinidas: ${it.message}")
                onResult(emptyList())
            }
    }
}