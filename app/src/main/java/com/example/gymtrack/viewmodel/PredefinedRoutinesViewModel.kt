package com.example.gymtrack.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/*
PredefinedRoutinesViewModel.kt

Este archivo define el ViewModel encargado de gestionar las rutinas predefinidas en la app GymTrack.

Las rutinas predefinidas están almacenadas en la colección "rutinasPredefinidas" de Firebase Firestore, y están disponibles para todos los usuarios.
Este ViewModel permite recuperarlas y mostrarlas en la interfaz de usuario a través de un flujo reactivo basado en StateFlow.

Este patrón permite que Jetpack Compose actualice automáticamente la interfaz cuando los datos cambien,
y permite a otros componentes recibir los resultados mediante un callback.
*/

class PredefinedRoutinesViewModel : ViewModel() {

    // Instancia de Firebase Firestore usada para acceder a la base de datos
    private val db = FirebaseFirestore.getInstance()

    // StateFlow mutable que contiene la lista de rutinas predefinidas cargadas
    // Es privado para que solo este ViewModel pueda modificarlo
    private val _routines = MutableStateFlow<List<RoutineData>>(emptyList())

    // StateFlow de solo lectura expuesto al exterior (por ejemplo, a la interfaz de usuario)
    // De este modo, la UI puede observar los cambios sin poder modificar directamente los datos
    val routines: StateFlow<List<RoutineData>> get() = _routines

    /**
     * Función pública que recupera todas las rutinas predefinidas desde Firestore.
     * Al completarse con éxito, actualiza el flujo _routines y ejecuta el callback onResult.
     *
     * @param onResult Callback opcional que se llama con la lista de rutinas obtenidas.
     */
    fun fetchRoutines(onResult: (List<RoutineData>) -> Unit) {
        db.collection("rutinasPredefinidas") // Accede a la colección 'rutinasPredefinidas'
            .get() // Realiza la consulta para obtener todos los documentos
            .addOnSuccessListener { result ->

                // Mapea cada documento a un objeto RoutineData
                // y se asegura de incluir el campo "esFavorita" (aunque normalmente es false aquí)
                val routines = result.mapNotNull { doc ->
                    doc.toObject(RoutineData::class.java).copy(
                        esFavorita = doc.getBoolean("esFavorita") ?: false
                    )
                }

                // Actualiza el flujo con los datos recuperados
                _routines.value = routines

                // Llama al callback externo con la lista de rutinas cargadas
                onResult(routines)
            }
            .addOnFailureListener {
                // En caso de fallo, muestra el error en consola y devuelve una lista vacía
                println("❌ Error al cargar rutinas predefinidas: ${it.message}")
                onResult(emptyList())
            }
    }
}
