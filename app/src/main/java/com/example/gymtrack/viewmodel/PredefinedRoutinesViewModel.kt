package com.example.gymtrack.viewmodel

/*
PredefinedRoutinesViewModel.kt

Este archivo define el ViewModel encargado de gestionar las rutinas predefinidas en la app GymTrack.
Se conecta a la base de datos Firestore para recuperar las rutinas almacenadas en la colección "rutinasPredefinidas".
Utiliza StateFlow para exponer de forma reactiva la lista de rutinas que se muestran al usuario.
Este ViewModel es útil cuando el usuario desea explorar rutinas ya preparadas y añadirlas a su colección personal.
*/

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Definición del ViewModel que controla las rutinas predefinidas
class PredefinedRoutinesViewModel : ViewModel() {

    // Instancia de la base de datos Firestore
    private val db = FirebaseFirestore.getInstance()

    // Flujo mutable donde se almacenan las rutinas predefinidas que se cargan desde Firestore
    private val _routines = MutableStateFlow<List<RoutineData>>(emptyList())

    // Flujo de solo lectura que expone la lista de rutinas a la interfaz de usuario
    val routines: StateFlow<List<RoutineData>> get() = _routines

    /**
     * Función que se encarga de recuperar todas las rutinas predefinidas almacenadas en Firebase.
     * Esta función usa la colección 'rutinasPredefinidas' y actualiza el flujo con los datos obtenidos.
     * Además, llama al callback onResult con la lista para que otros componentes puedan reaccionar.
     */
    fun fetchRoutines(onResult: (List<RoutineData>) -> Unit) {
        db.collection("rutinasPredefinidas") // Accede a la colección "rutinasPredefinidas"
            .get() // Solicita todos los documentos dentro de esa colección
            .addOnSuccessListener { result -> // Si la operación fue exitosa...
                // Convierte cada documento en un objeto de tipo RoutineData
                val routines = result.mapNotNull { doc ->
                    doc.toObject(RoutineData::class.java).copy(
                        esFavorita = doc.getBoolean("esFavorita") ?: false
                    )
                }
                _routines.value = routines // Actualiza el StateFlow con la lista de rutinas
                onResult(routines) // Ejecuta el callback para notificar que la carga fue exitosa
            }
            .addOnFailureListener {
                // Si ocurre un error, se muestra un mensaje en consola
                println("❌ Error al cargar rutinas predefinidas: ${it.message}")
                onResult(emptyList()) // Devuelve una lista vacía como fallback
            }
    }
}