plugins {
    alias(libs.plugins.android.application)        // Plugin principal para apps Android
    alias(libs.plugins.kotlin.android)             // Permite usar Kotlin en Android
    alias(libs.plugins.kotlin.compose)             // Soporte para Jetpack Compose
    alias(libs.plugins.google.services)            // Necesario para usar Firebase (lee google-services.json)
    id("kotlin-parcelize") // ✅ ESTA LÍNEA
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
    implementation("com.google.android.gms:play-services-auth:21.3.0")


    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.8") // Usa versión acorde a Compose

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Add the dependencies for the Firebase modules you want to use
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

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

