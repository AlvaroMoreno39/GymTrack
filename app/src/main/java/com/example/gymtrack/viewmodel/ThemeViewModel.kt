package com.example.gymtrack.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtrack.ui.theme.ThemePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    // 🔁 Para recordar el valor en DataStore (si lo necesitas en otros sitios)
    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode

    // ✅ Este se usa para recomposición instantánea en Compose
    val darkModeState = mutableStateOf(false)

    // ⏳ Para saber cuándo ya está cargado el valor guardado
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    init {
        viewModelScope.launch {
            val storedMode = ThemePreferences.readDarkMode(application)
            _darkMode.value = storedMode
            darkModeState.value = storedMode
            _isReady.value = true // ✅ Ya se ha cargado desde DataStore
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _darkMode.value = enabled
        darkModeState.value = enabled // ✅ Fuerza recomposición en tiempo real
        viewModelScope.launch {
            ThemePreferences.saveDarkMode(getApplication(), enabled)
        }
    }
}