package com.example.gymtrack.viewmodel

import android.content.Context
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/*
RoutineViewModel.kt

Este archivo define el modelo de vista (ViewModel) para gestionar las rutinas de entrenamiento en la app GymTrack.
Contiene la lógica para guardar, obtener, eliminar y copiar rutinas desde Firebase Firestore.
Está vinculado al modelo de datos `Exercise` y `RoutineData`, los cuales representan ejercicios individuales y rutinas completas respectivamente.
Todas las operaciones están asociadas al usuario autenticado mediante FirebaseAuth.
*/

@Parcelize
data class Exercise(
    val id: String = UUID.randomUUID().toString(), // <<--- nuevo ID único
    val nombre: String = "",
    val grupoMuscular: String = "",
    val tipo: String = "",
    val series: Int = 0,
    val reps: Int = 0,
    val duracion: Int = 0,
    val intensidad: String = ""
) : Parcelable


@Parcelize
data class RoutineData(
    val nombreRutina: String = "",                  // Nombre de la rutina
    val userId: String = "",                        // ID del usuario que la ha creado
    val fechaCreacion: Timestamp = Timestamp.now(), // Fecha de creación de la rutina
    val ejercicios: List<Exercise> = emptyList(),   // Lista de ejercicios que la componen
    val esFavorita: Boolean = false,                // Indica si la rutina está marcada como favorita
    val nivel: String? = null                       // Nivel de dificultad (usado en rutinas predefinidas)
) : Parcelable

class RoutineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _predefinedRoutines = MutableStateFlow<List<RoutineData>>(emptyList())
    val predefinedRoutines: StateFlow<List<RoutineData>> get() = _predefinedRoutines

    fun toggleFavorite(routineId: String, isFavorite: Boolean, onResult: (Boolean) -> Unit) {
        db.collection("rutinas").document(routineId)
            .update("esFavorita", isFavorite)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getUserRoutines(onResult: (List<Pair<String, RoutineData>>) -> Unit) {
        val user = auth.currentUser ?: return
        db.collection("rutinas")
            .whereEqualTo("userId", user.uid)
            .get()
            .addOnSuccessListener { result ->
                val routines = result.mapNotNull { doc ->
                    val rutina = doc.toObject(RoutineData::class.java).copy(
                        esFavorita = doc.getBoolean("esFavorita") ?: false
                    )
                    doc.id to rutina
                }
                onResult(routines)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun fetchPredefinedRoutines(onResult: (List<RoutineData>) -> Unit) {
        db.collection("rutinasPredefinidas")
            .get()
            .addOnSuccessListener { result ->
                val routines = result.mapNotNull { doc ->
                    doc.toObject(RoutineData::class.java).copy(
                        esFavorita = doc.getBoolean("esFavorita") ?: false
                    )
                }
                _predefinedRoutines.value = routines
                onResult(routines)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun saveFullRoutine(nombreRutina: String, ejercicios: List<Exercise>, onResult: (Boolean) -> Unit) {
        val user = auth.currentUser ?: return onResult(false)
        val rutina = RoutineData(nombreRutina, user.uid, Timestamp.now(), ejercicios)
        db.collection("rutinas")
            .add(rutina)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteRoutine(routineId: String, onResult: (Boolean) -> Unit) {
        db.collection("rutinas").document(routineId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun copyPredefinedRoutineToUser(nombreRutina: String, ejercicios: List<Exercise>, onResult: (Boolean) -> Unit) {
        val user = auth.currentUser ?: return onResult(false)
        val rutina = RoutineData(nombreRutina, user.uid, Timestamp.now(), ejercicios)
        db.collection("rutinas")
            .add(rutina)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun addExerciseToRoutine(routineId: String, nuevoEjercicio: Exercise, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("rutinas").document(routineId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val rutina = snapshot.toObject(RoutineData::class.java) ?: throw Exception("Rutina no encontrada")
            val updatedList = rutina.ejercicios + nuevoEjercicio
            transaction.update(docRef, "ejercicios", updatedList)
        }.addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteExerciseFromRoutineById(routineId: String, ejercicioId: String, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("rutinas").document(routineId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val rutina = snapshot.toObject(RoutineData::class.java) ?: throw Exception("Rutina no encontrada")
            val updatedList = rutina.ejercicios.filter { it.id != ejercicioId }
            transaction.update(docRef, "ejercicios", updatedList)
        }.addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateExerciseInRoutineById(routineId: String, ejercicioId: String, updatedExercise: Exercise, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("rutinas").document(routineId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val rutina = snapshot.toObject(RoutineData::class.java) ?: throw Exception("Rutina no encontrada")
            val updatedList = rutina.ejercicios.map { if (it.id == ejercicioId) updatedExercise else it }
            transaction.update(docRef, "ejercicios", updatedList)
        }.addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun savePredefinedRoutine(nombreRutina: String, ejercicios: List<Exercise>, nivel: String, onResult: (Boolean) -> Unit) {
        val rutina = hashMapOf(
            "nombreRutina" to nombreRutina,
            "userId" to "admin",
            "fechaCreacion" to Timestamp.now(),
            "ejercicios" to ejercicios,
            "nivel" to nivel
        )
        db.collection("rutinasPredefinidas")
            .add(rutina)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deletePredefinedRoutine(nombreRutina: String, onResult: (Boolean) -> Unit) {
        db.collection("rutinasPredefinidas")
            .whereEqualTo("nombreRutina", nombreRutina)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) return@addOnSuccessListener onResult(false)
                val docId = result.documents[0].id
                db.collection("rutinasPredefinidas").document(docId)
                    .delete()
                    .addOnSuccessListener { onResult(true) }
                    .addOnFailureListener { onResult(false) }
            }
            .addOnFailureListener { onResult(false) }
    }

    fun addExerciseToPredefinedRoutine(nombreRutina: String, nuevoEjercicio: Exercise, onResult: (Boolean) -> Unit) {
        db.collection("rutinasPredefinidas")
            .whereEqualTo("nombreRutina", nombreRutina)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) return@addOnSuccessListener onResult(false)
                val doc = result.documents[0]
                val rutina = doc.toObject(RoutineData::class.java) ?: return@addOnSuccessListener onResult(false)
                val updatedList = rutina.ejercicios + nuevoEjercicio
                doc.reference.update("ejercicios", updatedList)
                    .addOnSuccessListener { onResult(true) }
                    .addOnFailureListener { onResult(false) }
            }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteExerciseFromPredefinedRoutineById(nombreRutina: String, ejercicioId: String, onResult: (Boolean) -> Unit) {
        db.collection("rutinasPredefinidas")
            .whereEqualTo("nombreRutina", nombreRutina)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) return@addOnSuccessListener onResult(false)
                val doc = result.documents[0]
                val rutina = doc.toObject(RoutineData::class.java) ?: return@addOnSuccessListener onResult(false)
                val updatedList = rutina.ejercicios.filter { it.id != ejercicioId }
                doc.reference.update("ejercicios", updatedList)
                    .addOnSuccessListener { onResult(true) }
                    .addOnFailureListener { onResult(false) }
            }
            .addOnFailureListener { onResult(false) }
    }

    fun updateExerciseInPredefinedRoutineById(nombreRutina: String, ejercicioId: String, updatedExercise: Exercise, onResult: (Boolean) -> Unit) {
        db.collection("rutinasPredefinidas")
            .whereEqualTo("nombreRutina", nombreRutina)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) return@addOnSuccessListener onResult(false)
                val doc = result.documents[0]
                val rutina = doc.toObject(RoutineData::class.java) ?: return@addOnSuccessListener onResult(false)
                val updatedList = rutina.ejercicios.map { if (it.id == ejercicioId) updatedExercise else it }
                doc.reference.update("ejercicios", updatedList)
                    .addOnSuccessListener { onResult(true) }
                    .addOnFailureListener { onResult(false) }
            }
            .addOnFailureListener { onResult(false) }
    }
}

