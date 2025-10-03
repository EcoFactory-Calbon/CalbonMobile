// DENTRO DE build.gradle.kts (Módulo: app)

plugins {
    // PLUGINS CORRIGIDOS (Usando alias)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)

    // ESSENCIAL: Ativa o Parcelize para usar @Parcelize na data class
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.calbon"
    compileSdk = 35

    lint {
        abortOnError = false
    }

    defaultConfig {
        applicationId = "com.example.calbon"
        minSdk = 29
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.ai)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.fragment:fragment-ktx:1.8.0")
    implementation ("io.github.chaosleung:pinview:1.4.4")
    implementation("com.google.firebase:firebase-auth:24.0.1")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation ("com.google.android.material:material:1.x.x")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("androidx.cardview:cardview:1.0.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation ("com.google.android.material:material:1.11.0")

}
