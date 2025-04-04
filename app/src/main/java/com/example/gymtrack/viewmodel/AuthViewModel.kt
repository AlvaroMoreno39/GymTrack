package com.example.gymtrack.viewmodel

// ViewModel y corrutinas
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

// Firebase Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// Estado reactivo (StateFlow)
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la autenticación de usuario con Firebase.
 * Exponer estado reactivo para usuario actual y posibles errores.
 */
class AuthViewModel : ViewModel() {

    // Instancia de FirebaseAuth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Estado reactivo del usuario actual (null si no hay sesión)
    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> get() = _user

    // Estado reactivo para errores en login/registro/etc
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    /**
     * Inicia sesión con correo y contraseña.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
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
     * Registra un nuevo usuario con correo y contraseña.
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
     * Envía un correo para recuperar la contraseña.
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    _error.value = if (task.isSuccessful) {
                        "Correo de recuperación enviado"
                    } else {
                        task.exception?.message
                    }
                }
        }
    }

    /**
     * Cierra la sesión actual.
     */
    fun logout() {
        auth.signOut()
        _user.value = null
    }

    /**
     * Muestra error.
     */
    fun setError(message: String) {
        _error.value = message
    }

}