package com.example.gymtrack.viewmodel

/*
RoutineViewModel.kt

Este archivo define el modelo de vista (ViewModel) para gestionar las rutinas de entrenamiento en la app GymTrack.
Contiene la lógica para guardar, obtener, eliminar y copiar rutinas desde Firebase Firestore.
Está vinculado al modelo de datos `Exercise` y `RoutineData`, los cuales representan ejercicios individuales y rutinas completas respectivamente.
Todas las operaciones están asociadas al usuario autenticado mediante FirebaseAuth.
*/

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

// Modelo de datos que representa un ejercicio individual dentro de una rutina
data class Exercise(
    val nombre: String = "",             // Nombre del ejercicio (p. ej. Press de banca)
    val grupoMuscular: String = "",     // Grupo muscular trabajado (p. ej. Pecho)
    val tipo: String = "",              // Tipo de ejercicio (fuerza, cardio, etc.)
    val series: Int = 0,                // Número de series
    val reps: Int = 0,                  // Número de repeticiones
    val duracion: Int = 0,              // Duración en segundos (en caso de ser cardio)
    val intensidad: String = "",        // Intensidad del ejercicio (Alta, Media, Baja)
    val peso: Int = 0                   // Peso utilizado en kg
)

// Modelo de datos que representa una rutina completa con una lista de ejercicios
data class RoutineData(
    val nombreRutina: String = "",      // Nombre de la rutina (p. ej. Rutina Pecho Lunes)
    val userId: String = "",            // ID del usuario que creó la rutina
    val fechaCreacion: Timestamp = Timestamp.now(),  // Fecha y hora de creación
    val ejercicios: List<Exercise> = emptyList()      // Lista de ejercicios que forman la rutina
)

// ViewModel que maneja toda la lógica de operaciones sobre rutinas (CRUD)
class RoutineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()   // Acceso a la base de datos Firestore
    private val auth = FirebaseAuth.getInstance()      // Acceso a la autenticación del usuario

    /**
     * Guarda una rutina completa en la colección "rutinas" para el usuario actual.
     * Si no hay usuario logueado, muestra un mensaje de advertencia.
     */
    fun saveFullRoutine(
        nombreRutina: String,
        ejercicios: List<Exercise>,
        onResult: (Boolean) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onResult(false) // No hay usuario
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
            .addOnSuccessListener { onResult(true) } // Éxito
            .addOnFailureListener { onResult(false) } // Error
    }

    /**
     * Recupera todas las rutinas del usuario autenticado y las devuelve como una lista de pares:
     * el ID del documento y el objeto RoutineData correspondiente.
     */
    fun getUserRoutines(onResult: (List<Pair<String, RoutineData>>) -> Unit) {
        val currentUser = auth.currentUser ?: return // Si no hay usuario, salir

        db.collection("rutinas")
            .whereEqualTo("userId", currentUser.uid) // Solo rutinas del usuario actual
            .get()
            .addOnSuccessListener { result ->
                // Mapea cada documento en un par (ID, rutina)
                val routines = result.mapNotNull { doc ->
                    val rutina = doc.toObject(RoutineData::class.java)
                    doc.id to rutina
                }
                onResult(routines) // Devuelve los datos a través del callback
            }
    }

    /**
     * Elimina una rutina concreta en base al ID proporcionado.
     * Informa al llamador si se eliminó con éxito (true) o no (false).
     */
    fun deleteRoutine(routineId: String, onResult: (Boolean) -> Unit) {
        db.collection("rutinas")
            .document(routineId) // Busca el documento por ID
            .delete() // Elimina el documento
            .addOnSuccessListener { onResult(true) } // Si va bien, devuelve true
            .addOnFailureListener { onResult(false) } // Si falla, devuelve false
    }

    /**
     * Copia una rutina predefinida a la colección personal del usuario logueado.
     * Esto permite que el usuario edite su propia copia independiente.
     */
    fun copyPredefinedRoutineToUser(
        nombreRutina: String,
        ejercicios: List<Exercise>,
        onResult: (Boolean) -> Unit
    ) {
        val currentUser = auth.currentUser ?: return // Si no hay usuario, salir

        // Crea la rutina para insertar
        val rutina = RoutineData(
            nombreRutina = nombreRutina,
            userId = currentUser.uid,
            fechaCreacion = Timestamp.now(),
            ejercicios = ejercicios
        )

        // Añade la nueva rutina a la colección del usuario
        db.collection("rutinas")
            .add(rutina)
            .addOnSuccessListener { onResult(true) } // Indica éxito
            .addOnFailureListener { onResult(false) } // Indica fallo
    }
}