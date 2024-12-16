plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.healthtracker.chw"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.healthtracker.chw"
        minSdk = 33
        targetSdk = 36
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0")

    // Fragment
    implementation("androidx.fragment:fragment:1.6.2")

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // CardView
    implementation("androidx.cardview:cardview:1.0.0")

    // SharedPreferences
    implementation("androidx.preference:preference:1.2.1")
    implementation(libs.activity)

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-rxjava3:2.6.1")
    
    // Annotation processor for Room (Java)
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // WorkManager for offline sync
    implementation("androidx.work:work-runtime:2.9.0")

    // Location Services
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Google Maps clustering
    implementation("com.google.maps.android:android-maps-utils:3.8.2")

    // Retrofit for REST API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Note: WebSocket support is built into OkHttp 3.5+, no need for okhttp-ws

    // JSON processing
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}