package com.example.gymtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gymtrack.navigation.GymTrackApp
import com.example.gymtrack.ui.theme.GymTrackTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ❗ Cierra sesión si hay un usuario cacheado
        // FirebaseAuth.getInstance().signOut()

        enableEdgeToEdge()

        setContent {
            GymTrackTheme {
                GymTrackApp() // <- Este composable debería estar llamando a tu Scaffold con la navegación
            }
        }
    }
}

