package com.example.gymtrack.viewmodel

/*
RoutineViewModel.kt

Este archivo define el modelo de vista (ViewModel) para gestionar las rutinas de entrenamiento en la app GymTrack.
Contiene la lógica para guardar, obtener, eliminar y copiar rutinas desde Firebase Firestore.
Está vinculado al modelo de datos `Exercise` y `RoutineData`, los cuales representan ejercicios individuales y rutinas completas respectivamente.
Todas las operaciones están asociadas al usuario autenticado mediante FirebaseAuth.
*/

import android.content.Context
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Exercise(
    val nombre: String = "",
    val grupoMuscular: String = "",
    val tipo: String = "",
    val series: Int = 0,
    val reps: Int = 0,
    val duracion: Int = 0,
    val intensidad: String = "",
    val peso: Int = 0
) : Parcelable

@Parcelize
data class RoutineData(
    val nombreRutina: String = "",
    val userId: String = "",
    val fechaCreacion: Timestamp = Timestamp.now(),
    val ejercicios: List<Exercise> = emptyList()
) : Parcelable

// ViewModel que maneja toda la lógica de operaciones sobre rutinas (CRUD)
class RoutineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun saveFullRoutine(
        nombreRutina: String,
        ejercicios: List<Exercise>,
        onResult: (Boolean) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("RoutineViewModel", "❌ No hay usuario logueado")
            onResult(false)
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
                Log.d("RoutineViewModel", "✅ Rutina guardada con éxito")
                onResult(true)
            }
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al guardar rutina: ${it.message}")
                onResult(false)
            }
    }

    fun getUserRoutines(onResult: (List<Pair<String, RoutineData>>) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("RoutineViewModel", "❌ No hay usuario logueado al obtener rutinas")
            return
        }

        db.collection("rutinas")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { result ->
                val routines = result.mapNotNull { doc ->
                    val rutina = doc.toObject(RoutineData::class.java)
                    Log.d("RoutineViewModel", "📄 Rutina encontrada: ${rutina.nombreRutina} (UID: ${rutina.userId})")
                    doc.id to rutina
                }
                Log.d("RoutineViewModel", "✅ Total rutinas obtenidas: ${routines.size}")
                onResult(routines)
            }
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al obtener rutinas: ${it.message}")
            }
    }

    fun deleteRoutine(routineId: String, onResult: (Boolean) -> Unit) {
        db.collection("rutinas")
            .document(routineId)
            .delete()
            .addOnSuccessListener {
                Log.d("RoutineViewModel", "🗑️ Rutina eliminada correctamente")
                onResult(true)
            }
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al eliminar rutina: ${it.message}")
                onResult(false)
            }
    }

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
            .addOnSuccessListener {
                Log.d("RoutineViewModel", "📄 Copia de rutina predefinida guardada")
                onResult(true)
            }
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al copiar rutina: ${it.message}")
                onResult(false)
            }
    }

    fun addExerciseToRoutine(routineId: String, nuevoEjercicio: Exercise, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("rutinas").document(routineId)

        docRef.get()
            .addOnSuccessListener { document ->
                val rutina = document.toObject(RoutineData::class.java)
                if (rutina != null) {
                    val ejerciciosActualizados = rutina.ejercicios.toMutableList().apply {
                        add(nuevoEjercicio)
                    }

                    docRef.update("ejercicios", ejerciciosActualizados)
                        .addOnSuccessListener {
                            Log.d("RoutineViewModel", "✅ Ejercicio añadido correctamente")
                            onResult(true)
                        }
                        .addOnFailureListener {
                            Log.e("RoutineViewModel", "❌ Error al actualizar ejercicios: ${it.message}")
                            onResult(false)
                        }
                } else {
                    Log.e("RoutineViewModel", "❌ Rutina nula al añadir ejercicio")
                    onResult(false)
                }
            }
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al añadir ejercicio: ${it.message}")
                onResult(false)
            }
    }

    fun deleteExerciseFromRoutine(routineId: String, ejercicioIndex: Int) {
        val docRef = db.collection("rutinas").document(routineId)

        docRef.get()
            .addOnSuccessListener { document ->
                val rutina = document.toObject(RoutineData::class.java)
                if (rutina != null && rutina.ejercicios.size > ejercicioIndex) {
                    val ejerciciosActualizados = rutina.ejercicios.toMutableList().apply {
                        removeAt(ejercicioIndex)
                    }

                    docRef.update("ejercicios", ejerciciosActualizados)
                        .addOnSuccessListener {
                            Log.d("RoutineViewModel", "✅ Ejercicio eliminado correctamente")
                        }
                        .addOnFailureListener {
                            Log.e("RoutineViewModel", "❌ Error al eliminar ejercicio: ${it.message}")
                        }
                } else {
                    Log.e("RoutineViewModel", "❌ Rutina vacía o índice inválido al eliminar ejercicio")
                }
            }
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al eliminar ejercicio: ${it.message}")
            }
    }

    fun savePredefinedRoutine(
        nombreRutina: String,
        ejercicios: List<Exercise>,
        onResult: (Boolean) -> Unit
    ) {
        val rutina = RoutineData(
            nombreRutina = nombreRutina,
            userId = "admin",
            fechaCreacion = Timestamp.now(),
            ejercicios = ejercicios
        )

        db.collection("rutinasPredefinidas")
            .add(rutina)
            .addOnSuccessListener {
                Log.d("RoutineViewModel", "✅ Rutina predefinida guardada")
                onResult(true)
            }
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al guardar rutina predefinida: ${it.message}")
                onResult(false)
            }
    }

    fun deletePredefinedRoutine(nombreRutina: String, onResult: (Boolean) -> Unit) {
        db.collection("rutinasPredefinidas")
            .whereEqualTo("nombreRutina", nombreRutina)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val docId = result.documents[0].id
                    db.collection("rutinasPredefinidas").document(docId)
                        .delete()
                        .addOnSuccessListener { onResult(true) }
                        .addOnFailureListener { onResult(false) }
                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener { onResult(false) }
    }

}
