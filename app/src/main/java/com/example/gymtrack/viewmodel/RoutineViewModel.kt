package com.example.gymtrack.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RoutineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()       // Acceso a Firestore
    private val auth = FirebaseAuth.getInstance()          // Acceso a Firebase Auth

    // Guarda una rutina en Firestore
    fun addRoutine(
        context: Context,                                  // Contexto para mostrar Toast
        name: String,
        group: String,
        type: String,
        series: String,
        reps: String,
        duration: String,
        intensity: String
    ) {
        val currentUser = auth.currentUser ?: run {
            Toast.makeText(context, "⚠️ Usuario no logueado", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = currentUser.uid

        // Creamos el mapa de datos
        val routine = hashMapOf(
            "exerciseName" to name,
            "muscleGroup" to group,
            "type" to type,
            "series" to series,
            "reps" to reps,
            "duration" to duration,
            "intensity" to intensity,
            "userId" to uid
        )

        // Guardamos en Firestore
        db.collection("rutinas")
            .add(routine)
            .addOnSuccessListener {
                Toast.makeText(context, "✅ Rutina guardada", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "❌ Error al guardar rutina: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Añade esta función en tu RoutineViewModel
    fun getUserRoutines(onResult: (List<Map<String, Any>>) -> Unit) {
        val currentUser = auth.currentUser ?: return

        db.collection("rutinas")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                val routines = result.documents.map { doc ->
                    val routine = doc.data ?: emptyMap()
                    routine + mapOf("id" to doc.id) // ⬅️ Añadimos el ID del documento
                }
                onResult(routines)
            }
    }

    fun deleteRoutine(routineId: String, onResult: (Boolean) -> Unit) {
        db.collection("rutinas").document(routineId)
            .delete()
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun copyPredefinedRoutineToUser(routine: Map<String, Any>, onResult: (Boolean) -> Unit) {
        val currentUser = auth.currentUser ?: return

        val userRoutine = hashMapOf(
            "exerciseName" to routine["exerciseName"],
            "muscleGroup" to routine["muscleGroup"],
            "type" to routine["type"],
            "series" to routine["series"],
            "reps" to routine["reps"],
            "duration" to routine["duration"],
            "intensity" to routine["intensity"],
            "userId" to currentUser.uid
        )

        db.collection("rutinas")
            .add(userRoutine)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }



}