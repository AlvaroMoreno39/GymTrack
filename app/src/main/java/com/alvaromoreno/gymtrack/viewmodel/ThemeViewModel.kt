package com.alvaromoreno.gymtrack.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alvaromoreno.gymtrack.ui.theme.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel encargado de gestionar el tema claro/oscuro de la app GymTrack.
// Usa DataStore para recordar la preferencia del usuario y MutableState/StateFlow para que Compose pueda reaccionar a los cambios en tiempo real.

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    // Flow que expone si el modo oscuro está activado o no (se usa para compartir estado entre distintas partes de la app si fuera necesario).
    private val _darkMode = MutableStateFlow(false)    // Valor mutable interno
    val darkMode: StateFlow<Boolean> = _darkMode       // Valor expuesto de solo lectura

    // Estado mutable que Jetpack Compose puede observar para recomponer automáticamente las UI que dependen del modo oscuro.
    val darkModeState = mutableStateOf(false)          // Se usa dentro de las composables para reaccionar a cambios

    // Indica si el valor del modo oscuro se ha cargado desde DataStore.
    // Esto permite evitar usar valores incorrectos antes de que se haya recuperado la preferencia guardada.
    private val _isReady = MutableStateFlow(false)     // Estado interno mutable
    val isReady: StateFlow<Boolean> = _isReady // Estado expuesto de solo lectura para saber si ya está inicializado

    // Al inicializar el ViewModel, se lanza una corrutina para leer el valor del modo oscuro guardado en DataStore.
    // Una vez leído, se actualizan tanto el Flow como el estado observable de Compose.
    init {
        viewModelScope.launch {
            // Lee la preferencia almacenada (true = modo oscuro activado)
            val storedMode = ThemePreferences.readDarkMode(application)

            // Actualiza el valor en el Flow (útil para otros componentes que escuchen este valor)
            _darkMode.value = storedMode

            // Actualiza el estado para que Compose recomponga las pantallas si es necesario
            darkModeState.value = storedMode

            // Marca que la preferencia ya está cargada (se puede usar para mostrar un splash screen hasta que esté listo)
            _isReady.value = true
        }
    }

    // Esta función se llama cuando el usuario cambia manualmente el modo oscuro/claro.
    // Actualiza tanto los estados como la preferencia almacenada en DataStore.
    fun toggleDarkMode(enabled: Boolean) {
        // Actualiza el Flow (útil para lógicas fuera de Compose)
        _darkMode.value = enabled

        // Actualiza el estado de Compose (útil para recomponer la interfaz)
        darkModeState.value = enabled

        // Guarda la preferencia de forma persistente en DataStore
        viewModelScope.launch {
            ThemePreferences.saveDarkMode(getApplication(), enabled)
        }
    }
}
