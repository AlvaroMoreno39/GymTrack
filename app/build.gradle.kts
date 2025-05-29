// -------------------------------------------
// PLUGINS UTILIZADOS EN ESTE MÓDULO
// -------------------------------------------
plugins {
    alias(libs.plugins.android.application)        // Plugin principal para compilar apps Android
    alias(libs.plugins.kotlin.android)             // Plugin para usar Kotlin en proyectos Android
    alias(libs.plugins.kotlin.compose)             // Habilita soporte para Jetpack Compose (UI declarativa moderna)
    alias(libs.plugins.google.services)            // Necesario para procesar google-services.json (Firebase)
    id("kotlin-parcelize")                         // Permite usar la anotación @Parcelize (pasa objetos entre pantallas)
}

// -------------------------------------------
// CONFIGURACIÓN DEL PROYECTO ANDROID
// -------------------------------------------
android {
    namespace = "com.alvaromoreno.gymtrack"     // Espacio de nombres del paquete principal
    compileSdk = 35                        // API nivel 35 para compilar (Android 14)

    defaultConfig {
        applicationId = "com.alvaromoreno.gymtrack"      // ID único de la app
        minSdk = 24                                 // Versión mínima de Android (Android 7.0)
        targetSdk = 35                              // API objetivo (Android 14)
        versionCode = 1                             // Código de versión interno
        versionName = "1.0"                         // Nombre visible de la versión

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Runner para tests instrumentados
    }

    buildTypes {
        release {
            isMinifyEnabled = false     // No minimizar el código en versión release (mejor para depuración inicial)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),  // Config básica de ProGuard
                "proguard-rules.pro"                                       // Reglas personalizadas
            )
        }
    }

    // Compatibilidad de compilación con Java 11
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Compatibilidad de Kotlin con Java 11
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true      // Habilita Jetpack Compose como sistema de UI
    }
}

// -------------------------------------------
// DEPENDENCIAS DEL PROYECTO
// -------------------------------------------
dependencies {
    implementation(libs.accompanist.navigation.animation)

    // Autenticación con cuentas de Google
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // Almacenamiento de preferencias con DataStore (reemplazo moderno de SharedPreferences)
    implementation("androidx.datastore:datastore-preferences:1.1.6")

    // Gestión de tareas en segundo plano (ideal para notificaciones, backups, etc.)
    implementation("androidx.work:work-runtime-ktx:2.10.1")

    // Soporte para fuentes de Google en Compose
    implementation("androidx.compose.ui:ui-text-google-fonts:1.8.1")

    // Firebase BoM (Bill of Materials): asegura versiones compatibles entre módulos Firebase
    implementation(platform(libs.firebase.bom))

    // Material3 para Jetpack Compose (diseño visual moderno)
    implementation("androidx.compose.material3:material3:1.3.2")

    // Módulos de Firebase usados en la app
    implementation(libs.firebase.auth)        // Autenticación de usuarios
    implementation(libs.firebase.firestore)   // Base de datos NoSQL en la nube
    implementation(libs.firebase.storage)     // Almacenamiento de archivos (imágenes, etc.)
    implementation(libs.firebase.analytics)   // Analíticas de uso
    implementation(libs.firebase.messaging)   // Notificaciones push

    // Arquitectura moderna: ViewModel, LiveData, ciclo de vida
    implementation(libs.androidx.lifecycle.runtime.ktx)        // Integración con corrutinas
    implementation(libs.androidx.lifecycle.viewmodel.compose)  // ViewModel para Compose
    implementation(libs.androidx.lifecycle.livedata.ktx)       // LiveData para Compose

    // Navegación entre pantallas en Compose
    implementation(libs.androidx.navigation.compose)

    // Corrutinas Kotlin para tareas asíncronas y reactivas
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Iconos adicionales para BottomNavigation y botones
    implementation(libs.androidx.material.icons.extended)

    // Componentes básicos de Android (con extensiones Kotlin)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))       // BOM para Compose
    implementation(libs.androidx.ui)                          // UI básica de Compose
    implementation(libs.androidx.ui.graphics)                 // Gráficos y estilos
    implementation(libs.androidx.ui.tooling.preview)          // Vista previa de composables en diseño
    implementation(libs.androidx.material3)                   // Material 3 (repetido pero puede quedar así si BOM lo maneja)

    // ----------------------
    // DEPENDENCIAS PARA TEST
    // ----------------------
    testImplementation(libs.junit)                               // JUnit para test unitarios
    androidTestImplementation(libs.androidx.junit)               // Tests de Android
    androidTestImplementation(libs.androidx.espresso.core)       // Tests de UI con Espresso

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)      // Tests UI en Compose

    // ----------------------
    // DEPENDENCIAS DE DEBUG
    // ----------------------
    debugImplementation(libs.androidx.ui.tooling)                // Herramientas de UI en modo debug
    debugImplementation(libs.androidx.ui.test.manifest)          // Manifesto para pruebas UI
}
