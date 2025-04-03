plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services") // 🔥 Necesario para que Firebase funcione

}

android {
    namespace = "com.example.gymtrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gymtrack"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Plataforma BOM de Firebase para gestionar versiones
    implementation(libs.firebase.bom)

    // Servicios de Firebase que usaremos:
    implementation(libs.firebase.auth)      // Autenticación de usuarios (login, registro, etc.)
    implementation(libs.firebase.firestore) // Base de datos NoSQL en la nube para guardar rutinas, progreso, etc.

    // Librerías para arquitectura moderna (MVVM):
    implementation(libs.androidx.lifecycle.runtime.ktx)        // Ciclo de vida con soporte para corrutinas
    implementation(libs.androidx.lifecycle.viewmodel.compose)  // ViewModel adaptado a Jetpack Compose
    implementation(libs.androidx.lifecycle.livedata.ktx)       // LiveData (observación de estados reactivos)

    // Navegación entre pantallas con Compose
    implementation(libs.androidx.navigation.compose)

    //️ Corrutinas Kotlin para tareas asíncronas
    implementation(libs.kotlinx.coroutines.core)    // Lógica general (por ejemplo, acceso a datos)
    implementation(libs.kotlinx.coroutines.android) // Adaptación de corrutinas al ciclo de vida Android

    // Iconos extendidos de Material (por ejemplo, para BottomBar)
    implementation(libs.androidx.material.icons.extended)

    // Componentes básicos de Android
    implementation(libs.androidx.core.ktx)             // Extensiones de Kotlin para Android
    implementation(libs.androidx.activity.compose)     // Soporte de Jetpack Compose en actividades

    // Jetpack Compose (UI moderna con Material3)
    implementation(platform(libs.androidx.compose.bom))       // BOM para unificar versiones de Compose
    implementation(libs.androidx.ui)                          // Núcleo de Compose UI
    implementation(libs.androidx.ui.graphics)                 // Soporte para gráficos y estilos
    implementation(libs.androidx.ui.tooling.preview)          // Previsualización en tiempo real de composables
    implementation(libs.androidx.material3)                   // Diseño Material 3

    // Test unitarios y de interfaz
    testImplementation(libs.junit)                               // Tests unitarios (JUnit 4)
    androidTestImplementation(libs.androidx.junit)               // Tests Android específicos (instrumentación)
    androidTestImplementation(libs.androidx.espresso.core)       // Pruebas de UI con Espresso
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)      // Tests de Compose UI

    // Debug y tooling para desarrollo
    debugImplementation(libs.androidx.ui.tooling)                // Herramientas de UI en modo debug
    debugImplementation(libs.androidx.ui.test.manifest)          // Manifesto de pruebas para UI
}