package com.example.gymtrack.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

// Modelo de datos para representar un ejercicio
data class Exercise(
    val nombre: String = "",
    val grupoMuscular: String = "",
    val tipo: String = "",
    val series: Int = 0,
    val reps: Int = 0,
    val duracion: Int = 0,
    val intensidad: String = "",
    val peso: Int = 0
)

// Modelo de datos para una rutina completa
data class RoutineData(
    val nombreRutina: String = "",
    val userId: String = "",
    val fechaCreacion: Timestamp = Timestamp.now(),
    val ejercicios: List<Exercise> = emptyList()
)

class RoutineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Guarda una rutina completa con varios ejercicios en Firestore.
     */
    fun saveFullRoutine(
        context: Context,
        nombreRutina: String,
        ejercicios: List<Exercise>
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "⚠️ Usuario no logueado", Toast.LENGTH_SHORT).show()
            return
        }

        val rutina = RoutineData(
            nombreRutina = nombreRutina,
            userId = currentUser.uid,
            fechaCreacion = Timestamp.now(),
            ejercicios = ejercicios
        )

        db.collection("rutinas")
            .add(rutina)
            .addOnSuccessListener {
                Toast.makeText(context, "✅ Rutina guardada con éxito", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "❌ Error al guardar rutina: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Obtiene todas las rutinas del usuario actual.
     */
    fun getUserRoutines(onResult: (List<Pair<String, RoutineData>>) -> Unit) {
        val currentUser = auth.currentUser ?: return

        db.collection("rutinas")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                val routines = result.mapNotNull { doc ->
                    val rutina = doc.toObject(RoutineData::class.java)
                    doc.id to rutina
                }
                onResult(routines)
            }
    }

    /**
     * Elimina una rutina por su ID.
     */
    fun deleteRoutine(routineId: String, onResult: (Boolean) -> Unit) {
        db.collection("rutinas")
            .document(routineId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /**
     * Copia una rutina predefinida completa a la colección del usuario actual.
     */
    fun copyPredefinedRoutineToUser(
        nombreRutina: String,
        ejercicios: List<Exercise>,
        onResult: (Boolean) -> Unit
    ) {
        val currentUser = auth.currentUser ?: return

        val rutina = RoutineData(
            nombreRutina = nombreRutina,
            userId = currentUser.uid,
            fechaCreacion = Timestamp.now(),
            ejercicios = ejercicios
        )

        db.collection("rutinas")
            .add(rutina)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
