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

/*
RoutineViewModel.kt

Este archivo define el modelo de vista (ViewModel) para gestionar las rutinas de entrenamiento en la app GymTrack.
Contiene la lógica para guardar, obtener, eliminar y copiar rutinas desde Firebase Firestore.
Está vinculado al modelo de datos `Exercise` y `RoutineData`, los cuales representan ejercicios individuales y rutinas completas respectivamente.
Todas las operaciones están asociadas al usuario autenticado mediante FirebaseAuth.
*/

@Parcelize
data class Exercise(
    val nombre: String = "",              // Nombre del ejercicio
    val grupoMuscular: String = "",       // Grupo muscular trabajado
    val tipo: String = "",                // Tipo de ejercicio (fuerza, cardio, etc.)
    val series: Int = 0,                  // Número de series
    val reps: Int = 0,                    // Repeticiones por serie
    val duracion: Int = 0,                // Duración en segundos (si aplica, para cardio)
    val intensidad: String = "",          // Nivel de intensidad del ejercicio
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

    // --- Firebase ---
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // --- Rutinas personales ---

    // Marca una rutina como favorita o elimina esa marca
    fun toggleFavorite(routineId: String, isFavorite: Boolean, onResult: (Boolean) -> Unit) {
        db.collection("rutinas")
            .document(routineId)
            .update("esFavorita", isFavorite)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Recupera todas las rutinas del usuario actual desde Firestore
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
                    val rutina = doc.toObject(RoutineData::class.java).copy(
                        esFavorita = doc.getBoolean("esFavorita") ?: false
                    )
                    doc.id to rutina
                }
                Log.d("RoutineViewModel", "✅ Total rutinas obtenidas: ${routines.size}")
                onResult(routines)
            }
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al obtener rutinas: ${it.message}")
            }
    }

    // Guarda una nueva rutina creada por el usuario en la colección 'rutinas'
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

    // Elimina una rutina existente por su ID
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

    // Copia una rutina predefinida a la colección personal del usuario
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

    // Añade un nuevo ejercicio a una rutina existente
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

    // Elimina un ejercicio de una rutina de usuario por índice
    fun deleteExerciseFromRoutine(routineId: String, ejercicioIndex: Int, onResult: (Boolean) -> Unit) {
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
                            onResult(true)
                        }
                        .addOnFailureListener {
                            Log.e("RoutineViewModel", "❌ Error al eliminar ejercicio: ${it.message}")
                            onResult(false)
                        }
                } else {
                    Log.e("RoutineViewModel", "❌ Rutina vacía o índice inválido al eliminar ejercicio")
                    onResult(false)
                }
            }
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al eliminar ejercicio: ${it.message}")
                onResult(false)
            }
    }


    // StateFlow para rutinas predefinidas
    private val _predefinedRoutines = MutableStateFlow<List<RoutineData>>(emptyList())
    val predefinedRoutines: StateFlow<List<RoutineData>> get() = _predefinedRoutines

    // Recupera todas las rutinas predefinidas de la colección correspondiente
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
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al cargar rutinas predefinidas: ${it.message}")
                onResult(emptyList())
            }
    }

    // Guarda una rutina predefinida (usado por el administrador)
    fun savePredefinedRoutine(
        nombreRutina: String,
        ejercicios: List<Exercise>,
        nivel: String,
        onResult: (Boolean) -> Unit
    ) {
        val rutina = hashMapOf(
            "nombreRutina" to nombreRutina,
            "userId" to "admin",
            "fechaCreacion" to Timestamp.now(),
            "ejercicios" to ejercicios,
            "nivel" to nivel
        )
        db.collection("rutinasPredefinidas")
            .add(rutina)
            .addOnSuccessListener {
                Log.d("RoutineViewModel", "✅ Rutina predefinida guardada con nivel")
                onResult(true)
            }
            .addOnFailureListener {
                Log.e("RoutineViewModel", "❌ Error al guardar rutina predefinida: ${it.message}")
                onResult(false)
            }
    }

    // Elimina una rutina predefinida según su nombre
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

    // Elimina un ejercicio de una rutina predefinida por índice
    fun deleteExerciseFromPredefinedRoutine(nombreRutina: String, ejercicioIndex: Int, onResult: (Boolean) -> Unit) {
        val collection = db.collection("rutinasPredefinidas")
        collection.whereEqualTo("nombreRutina", nombreRutina)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val doc = result.documents[0]
                    val rutina = doc.toObject(RoutineData::class.java)
                    if (rutina != null && rutina.ejercicios.size > ejercicioIndex) {
                        val ejerciciosActualizados = rutina.ejercicios.toMutableList().apply {
                            removeAt(ejercicioIndex)
                        }
                        doc.reference.update("ejercicios", ejerciciosActualizados)
                            .addOnSuccessListener { onResult(true) }
                            .addOnFailureListener { onResult(false) }
                    } else {
                        onResult(false)
                    }
                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener { onResult(false) }
    }

    fun addExerciseToPredefinedRoutine(nombreRutina: String, nuevoEjercicio: Exercise, onResult: (Boolean) -> Unit) {
        db.collection("rutinasPredefinidas")
            .whereEqualTo("nombreRutina", nombreRutina)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val doc = result.documents[0]
                    val rutina = doc.toObject(RoutineData::class.java)
                    if (rutina != null) {
                        val ejerciciosActualizados = rutina.ejercicios.toMutableList().apply {
                            add(nuevoEjercicio)
                        }
                        doc.reference.update("ejercicios", ejerciciosActualizados)
                            .addOnSuccessListener { onResult(true) }
                            .addOnFailureListener { onResult(false) }
                    } else {
                        onResult(false)
                    }
                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener { onResult(false) }
    }

    // Actualiza un ejercicio en una rutina del usuario (por índice)
    fun updateExerciseInRoutine(
        routineId: String,
        index: Int,
        ejercicio: Exercise,
        onResult: (Boolean) -> Unit
    ) {
        val docRef = db.collection("rutinas").document(routineId)
        docRef.get()
            .addOnSuccessListener { document ->
                val rutina = document.toObject(RoutineData::class.java)
                if (rutina != null && rutina.ejercicios.size > index) {
                    val ejerciciosActualizados = rutina.ejercicios.toMutableList().apply {
                        set(index, ejercicio)
                    }
                    docRef.update("ejercicios", ejerciciosActualizados)
                        .addOnSuccessListener { onResult(true) }
                        .addOnFailureListener { onResult(false) }
                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener { onResult(false) }
    }

    // Actualiza un ejercicio en una rutina predefinida (por índice)
    fun updateExerciseInPredefinedRoutine(
        nombreRutina: String,
        index: Int,
        ejercicio: Exercise,
        onResult: (Boolean) -> Unit
    ) {
        db.collection("rutinasPredefinidas")
            .whereEqualTo("nombreRutina", nombreRutina)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val doc = result.documents[0]
                    val rutina = doc.toObject(RoutineData::class.java)
                    if (rutina != null && rutina.ejercicios.size > index) {
                        val ejerciciosActualizados = rutina.ejercicios.toMutableList().apply {
                            set(index, ejercicio)
                        }
                        doc.reference.update("ejercicios", ejerciciosActualizados)
                            .addOnSuccessListener { onResult(true) }
                            .addOnFailureListener { onResult(false) }
                    } else {
                        onResult(false)
                    }
                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener { onResult(false) }
    }


}
