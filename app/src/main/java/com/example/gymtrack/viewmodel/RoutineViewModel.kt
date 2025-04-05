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
}