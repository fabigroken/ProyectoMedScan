plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.dokka") version "1.9.20"
}

android {
    namespace = "com.fabigroken.proyectomedscan"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fabigroken.proyectomedscan"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.9")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.9")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-firestore")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /** Librería para reconocimiento de texto (OCR) */
    implementation("com.google.mlkit:text-recognition:16.0.0")

    /** Librería para leer códigos de barras */
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    /** Librería para hacer peticiones HTTP a APIs */
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    /** Convierte JSON a objetos Kotlin */
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    /** Librería OkHttp para hacer peticiones HTTP a Gemini */
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    /** Librería de Firebase Firestore */
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")

}


    tasks.dokkaHtml.configure {
        outputDirectory.set(file("$buildDir/dokka"))
    }
