package com.example.gymtrack.viewmodel

/*
AuthViewModel.kt

Este archivo define el ViewModel que gestiona toda la autenticación de usuarios en la app GymTrack utilizando Firebase Authentication.
Expone dos estados reactivos mediante StateFlow: el usuario actual (`user`) y los posibles errores (`error`).
Aquí se implementan las funcionalidades de login, registro, recuperación de contraseña y cierre de sesión.

Este ViewModel permite separar la lógica de autenticación del resto de la interfaz de usuario y mantiene el estado sincronizado.
*/

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Firebase Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
// Estado reactivo (StateFlow)
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    // Instancia de Firebase Authentication para acceder a las funciones de login, registro, etc.
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estado observable que representa el usuario actual autenticado (si está logueado, tendrá datos)
    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> get() = _user

    // Estado observable para mensajes de error en login, registro u otras operaciones
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    /**
     * Inicia sesión con correo y contraseña proporcionados.
     * Si la operación tiene éxito, se actualiza el usuario y se limpia el error.
     * Si falla, se guarda el mensaje de error en el flujo de estado.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _user.value = auth.currentUser // Usuario logueado correctamente
                        _error.value = null
                    } else {
                        _error.value = task.exception?.message // Guarda el mensaje de error
                    }
                }
        }
    }

    /**
     * Registra un nuevo usuario con correo y contraseña.
     * Si tiene éxito, se establece el usuario actual en el flujo.
     * Si no, se guarda el error devuelto por Firebase.
     */
    fun register(email: String, password: String) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _user.value = auth.currentUser
                        _error.value = null
                    } else {
                        _error.value = task.exception?.message
                    }
                }
        }
    }

    /**
     * Envía un correo de recuperación de contraseña al email proporcionado.
     * Actualiza el estado de error con un mensaje de éxito o de fallo.
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    _error.value = if (task.isSuccessful) {
                        "Correo de recuperación enviado" // Mensaje de confirmación
                    } else {
                        task.exception?.message // Mensaje de error
                    }
                }
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     * También muestra un Toast informando que se ha cerrado sesión.
     */
    fun logout(context: Context) {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
    }

    /**
     * Método auxiliar para establecer manualmente un mensaje de error.
     */
    fun setError(message: String) {
        _error.value = message
    }

}