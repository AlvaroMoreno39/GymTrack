package com.example.gymtrack.viewmodel

/*
GeneralProgressViewModel.kt

Este ViewModel se encarga de calcular y exponer los datos necesarios para mostrar el progreso general del usuario.
Consulta la colección de "rutinas" en Firestore y suma las series de ejercicios agrupadas por grupo muscular.
Expone dos StateFlow para que la UI pueda representar los datos de forma reactiva: uno con la suma de series por grupo muscular
y otro (opcionalmente) con el peso total por rutina, aunque actualmente no se está utilizando.

Esto sirve principalmente para alimentar gráficos en la pantalla de progreso general.
*/

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class GeneralProgressViewModel : ViewModel() {

    // Conexión a Firestore
    private val db = FirebaseFirestore.getInstance()

    // Este flujo reactivo podría usarse para mostrar el peso total por rutina (no está en uso en este código)
    private val _pesoTotalPorRutina = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val pesoTotalPorRutina: StateFlow<List<Pair<String, Float>>> = _pesoTotalPorRutina

    // Este flujo contiene los datos agregados por grupo muscular (clave = grupo, valor = total series)
    private val _datosPorGrupoMuscular = MutableStateFlow<Map<String, Float>>(emptyMap())
    val datosPorGrupoMuscular: StateFlow<Map<String, Float>> = _datosPorGrupoMuscular

    /**
     * Función que consulta las rutinas del usuario desde Firestore y calcula el total de series
     * que ha realizado por cada grupo muscular. Los datos se agrupan y se almacenan en el StateFlow.
     */
    fun cargarDatos(userId: String) {
        // Lanzamos una coroutine en el scope del ViewModel
        viewModelScope.launch {
            db.collection("rutinas")
                .whereEqualTo("userId", userId) // Solo rutinas del usuario actual
                .get()
                .addOnSuccessListener { result ->

                    // Mapa temporal para acumular las series por grupo muscular
                    val mapa = mutableMapOf<String, Float>()

                    // Iteramos por cada rutina
                    for (doc in result) {
                        // Recuperamos la lista de ejercicios de cada rutina
                        val ejercicios =
                            doc.get("ejercicios") as? List<Map<String, Any>> ?: continue

                        // Recorremos los ejercicios y sumamos series por grupo
                        for (ejercicio in ejercicios) {
                            val grupo = ejercicio["grupoMuscular"] as? String ?: continue
                            val series = (ejercicio["series"] as? Long)?.toFloat() ?: 0f

                            // Sumamos las series a ese grupo muscular
                            mapa[grupo] = mapa.getOrDefault(grupo, 0f) + series
                        }
                    }

                    // Actualizamos el flujo de estado con los datos finales
                    _datosPorGrupoMuscular.value = mapa
                }
        }
    }
}