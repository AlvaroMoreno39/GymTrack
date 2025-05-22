package com.example.gymtrack.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Este archivo define un objeto de utilidad para guardar y leer la preferencia del modo oscuro en la app GymTrack.
// Usa Jetpack DataStore (la alternativa moderna a SharedPreferences) para almacenar de forma persistente un valor booleano.

// DataStore se declara como una extensión del contexto para facilitar su acceso desde cualquier parte.
// "settings" es el nombre del archivo XML interno que almacenará las preferencias.
private val Context.dataStore by preferencesDataStore(name = "settings")

// Objeto singleton que encapsula el acceso a las preferencias de tema (modo claro/oscuro).
object ThemePreferences {

    // Clave con la que se guarda la preferencia de modo oscuro en el DataStore.
    // Almacena un valor booleano: true para oscuro, false para claro.
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    // Función suspendida para guardar la preferencia del modo oscuro.
    // Recibe el contexto y el valor booleano (true = modo oscuro activado).
    suspend fun saveDarkMode(context: Context, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            // Se guarda el valor en DataStore con la clave especificada.
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    // Función suspendida para leer la preferencia del modo oscuro.
    // Devuelve el valor almacenado, o false si no hay ninguno aún.
    suspend fun readDarkMode(context: Context): Boolean {
        return context.dataStore.data
            // Se accede al flujo de datos almacenado
            .map { preferences -> preferences[DARK_MODE_KEY] ?: false }
            // Se obtiene el primer valor emitido (el actual)
            .first()
    }
}
