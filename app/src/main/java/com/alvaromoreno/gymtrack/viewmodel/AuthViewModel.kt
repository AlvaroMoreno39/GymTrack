package com.alvaromoreno.gymtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Firebase Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
// Estado reactivo (StateFlow)
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/*
AuthViewModel.kt

Este archivo define el ViewModel que gestiona toda la autenticación de usuarios en la app GymTrack utilizando Firebase Authentication.
Expone dos estados observables mediante StateFlow: el usuario actual (`user`) y los posibles errores (`error`).

El objetivo principal es encapsular toda la lógica de autenticación en un único punto, manteniendo la interfaz de usuario desacoplada y reactiva.
Aquí se implementan las funcionalidades de login, registro, recuperación de contraseña y cierre de sesión.
*/

class AuthViewModel : ViewModel() {

    // Instancia de FirebaseAuth que gestiona todo el sistema de autenticación
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estado interno mutable que contiene el usuario actualmente autenticado (si lo hay)
    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)

    // Estado externo de solo lectura expuesto a la UI
    val user: StateFlow<FirebaseUser?> get() = _user

    // Estado interno mutable para guardar el mensaje de error más reciente
    private val _error = MutableStateFlow<String?>(null)

    // Estado externo de solo lectura que la UI puede observar para mostrar errores
    val error: StateFlow<String?> get() = _error

    /**
     * Intenta iniciar sesión con el correo y contraseña proporcionados.
     * Si la operación es exitosa, actualiza el usuario actual y limpia cualquier error.
     * Si falla, almacena el mensaje de error para que la UI lo muestre.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _user.value = auth.currentUser // Usuario autenticado correctamente
                        _error.value = null             // Limpiar errores anteriores
                    } else {
                        _error.value = task.exception?.message // Registrar mensaje de error
                    }
                }
        }
    }

    /**
     * Registra un nuevo usuario utilizando el correo y contraseña especificados.
     * Si la operación tiene éxito, se actualiza el usuario.
     * En caso de error, se expone el mensaje correspondiente.
     */
    fun register(email: String, password: String) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _user.value = auth.currentUser // Usuario registrado correctamente
                        _error.value = null            // Limpiar errores anteriores
                    } else {
                        _error.value = task.exception?.message // Registrar mensaje de error
                    }
                }
        }
    }

    /**
     * Envía un correo de recuperación de contraseña al email indicado.
     * Si tiene éxito, se guarda un mensaje de confirmación.
     * Si falla, se expone el mensaje de error devuelto por Firebase.
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    _error.value = if (task.isSuccessful) {
                        "Correo de recuperación enviado" // Confirmación exitosa
                    } else {
                        task.exception?.message         // Mensaje de error en caso de fallo
                    }
                }
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     * Actualiza el flujo de usuario a null para que la UI reaccione (ej. redirigir al login).
     */
    fun logout() {
        FirebaseAuth.getInstance().signOut()
        _user.value = null // Usuario desconectado, actualizar estado
    }

    /**
     * Establece manualmente un mensaje de error. Puede usarse desde otras capas (por ejemplo, validaciones).
     */
    fun setError(message: String) {
        _error.value = message
    }

    /**
     * Limpia el estado de error actual. Útil para reiniciar la pantalla o al cambiar de contexto.
     */
    fun clearError() {
        _error.value = null
    }
}

