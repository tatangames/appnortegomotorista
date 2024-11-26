plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.alcaldiasantaananorte.nortegomotorista"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.alcaldiasantaananorte.nortegomotorista"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.compose.material:material-icons-extended:1.7.2")

    implementation("androidx.compose.ui:ui:1.7.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.2")
    implementation("androidx.compose.material:material:1.7.2")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.2")

    implementation("com.github.GrenderG:Toasty:1.5.2")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.foundation:foundation:1.3.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")

    implementation("com.google.android.gms:play-services-auth:20.5.0")
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("com.google.android.gms:play-services-maps:19.0.0")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    implementation("com.google.android.gms:play-services-location:21.0.1")


    // Permissions and Utils
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("com.beust:klaxon:5.5")


    // Maps
    implementation("com.google.maps.android:maps-compose:4.3.3")
    implementation("com.github.prabhat1707:EasyWayLocation:2.4")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore")

    implementation("com.github.imperiumlabs:GeoFirestore-Android:v1.5.0")
    implementation("com.google.maps.android:android-maps-utils:2.3.0")




    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}