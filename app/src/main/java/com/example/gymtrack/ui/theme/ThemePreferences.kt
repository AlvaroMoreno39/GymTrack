package com.example.gymtrack.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 📦 DataStore instance (extensión de Context)
private val Context.dataStore by preferencesDataStore(name = "settings")

object ThemePreferences {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    // ✅ Guardar preferencia de modo oscuro
    suspend fun saveDarkMode(context: Context, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    // ✅ Leer preferencia de modo oscuro
    suspend fun readDarkMode(context: Context): Boolean {
        return context.dataStore.data
            .map { preferences -> preferences[DARK_MODE_KEY] ?: false }
            .first()
    }
}
