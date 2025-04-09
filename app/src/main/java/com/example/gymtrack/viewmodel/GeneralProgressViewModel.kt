package com.example.gymtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeneralProgressViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _pesoTotalPorRutina = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val pesoTotalPorRutina: StateFlow<List<Pair<String, Float>>> = _pesoTotalPorRutina

    private val _datosPorGrupoMuscular = MutableStateFlow<Map<String, Float>>(emptyMap())
    val datosPorGrupoMuscular: StateFlow<Map<String, Float>> = _datosPorGrupoMuscular

    fun cargarDatos(userId: String) {
        viewModelScope.launch {
            db.collection("rutinas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val mapa = mutableMapOf<String, Float>()

                    for (doc in result) {
                        val ejercicios = doc.get("ejercicios") as? List<Map<String, Any>> ?: continue

                        for (ejercicio in ejercicios) {
                            val grupo = ejercicio["grupoMuscular"] as? String ?: continue
                            val series = (ejercicio["series"] as? Long)?.toFloat() ?: 0f

                            mapa[grupo] = mapa.getOrDefault(grupo, 0f) + series
                        }
                    }

                    _datosPorGrupoMuscular.value = mapa
                }
        }
    }


}