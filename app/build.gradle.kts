import com.google.devtools.ksp.gradle.KspTaskJvm

plugins {
    id("idea")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.squareup.wire")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.anafthdev.story"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.anafthdev.story"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "STORY_BASE_URL", "\"https://story-api.dicoding.dev/\"")

            kotlinOptions {
                freeCompilerArgs += listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                    "-Xjvm-default=all"
                )
            }
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true

            buildConfigField("String", "STORY_BASE_URL", "\"https://story-api.dicoding.dev/\"")

            kotlinOptions {
                freeCompilerArgs += listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                    "-Xjvm-default=all"
                )
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
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
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

// Add this to fix ksp debug error when using wire and ksp
androidComponents {
    onVariants { variant ->
        // https://github.com/square/wire/issues/2335
        val buildType = variant.buildType.toString()
        val flavor = variant.flavorName.toString()
        tasks.withType<KspTaskJvm> {
            if (name.contains(buildType, ignoreCase = true) && name.contains(
                    flavor,
                    ignoreCase = true
                )
            ) {
                dependsOn("generate${flavor.capitalize()}${buildType.capitalize()}Protos")
            }
        }
    }
}

wire {
    kotlin {
        android = true
    }
}

dependencies {

    val lifecycle = "2.7.0"
    val datastore = "1.1.1"

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference:1.2.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    // Material Design
    implementation("com.google.android.material:material:1.12.0")

    // Datastore
    implementation("androidx.datastore:datastore:$datastore")
    implementation("androidx.datastore:datastore-preferences:$datastore")
    implementation("androidx.datastore:datastore-core:$datastore")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Work Manager
    implementation("androidx.hilt:hilt-work:1.2.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    ksp("com.google.dagger:hilt-compiler:2.48")
    ksp("com.google.dagger:hilt-android-compiler:2.48")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Other
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.squareup.wire:wire-runtime:4.4.3")
    implementation("com.github.bumptech.glide:glide:4.16.0")

//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
}