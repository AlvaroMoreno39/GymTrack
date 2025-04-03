// build.gradle.kts (proyecto raíz)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1") // ← 🔥 Añade esto
    }
}
