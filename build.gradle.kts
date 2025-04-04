// build.gradle.kts (proyecto raíz)
// Define plugins que estarán disponibles en los subproyectos (como :app)
plugins {
    alias(libs.plugins.android.application) apply false // Plugin para proyectos Android
    alias(libs.plugins.kotlin.android) apply false      // Soporte para Kotlin en Android
    alias(libs.plugins.kotlin.compose) apply false      // Jetpack Compose
}