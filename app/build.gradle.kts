plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.avatar_ai_manager"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.avatar_ai_manager"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add API as a Manifest Placeholder
        manifestPlaceholders["cloud_anchors_api_key"] =
            project.property("CLOUD_ANCHORS_API_KEY").toString()
    }

    buildTypes {
        all {
            // Load Google Cloud Anchors Management API from gradle.properties
            val clientId = project.property("CLIENT_ID")
            val clientEmail = project.property("CLIENT_EMAIL")
            val privateKey = project.property("PRIVATE_KEY")
            val privateKeyId = project.property("PRIVATE_KEY_ID")

            // Initialise the credentials as BuildConfig fields.
            buildConfigField("String", "CLIENT_ID", "$clientId")
            buildConfigField("String", "CLIENT_EMAIL", "$clientEmail")
            buildConfigField("String", "PRIVATE_KEY", "$privateKey")
            buildConfigField("String", "PRIVATE_KEY_ID", "$privateKeyId")
        }

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
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Cloud Storage and Database Module
    implementation(project(":avatar-ai-cloud-storage"))

    // Navigation libraries
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

    // Room Libraries
    implementation("androidx.room:room-runtime:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")

    // SceneView
    implementation("io.github.sceneview:arsceneview:0.10.0")
    // AR Core
    implementation("com.google.ar:core:1.38.0")

    // WEb Service Libraries
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    // Retrofit with Moshi Converter
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Google OAuth2 Authentication
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}