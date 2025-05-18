package com.example.gymtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.lifecycle.ViewModelProvider
import com.example.gymtrack.navigation.GymTrackApp
import com.example.gymtrack.ui.theme.GymTrackTheme
import com.example.gymtrack.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val themeViewModel = ViewModelProvider(this)[ThemeViewModel::class.java]

        setContent {
            val darkMode by themeViewModel.darkMode.collectAsState()
            val isReady by themeViewModel.isReady.collectAsState()

            if (isReady) {
                GymTrackApp(themeViewModel, darkMode)
            }
        }
    }
}

