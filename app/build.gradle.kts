import java.util.Properties

plugins {
    id("idea")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.squareup.wire")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
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

        val properties = Properties().apply {
            load(rootProject.file("local.properties").reader())
        }

        val mapsApiKey = properties["MAPS_API_KEY"]
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey.toString()
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
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

wire {
    kotlin {
        android = true
    }
}

dependencies {

    val lifecycle = "2.8.0"
    val datastore = "1.1.1"
    val espresso = "3.5.1"
    val room = "2.6.1"

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.7.1")
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
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Room
    implementation("androidx.room:room-runtime:$room")
    implementation("androidx.room:room-ktx:$room")
    implementation("androidx.room:room-paging:$room")
    kapt("androidx.room:room-compiler:$room")

    // Work Manager
    implementation("androidx.hilt:hilt-work:1.2.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    kapt("com.google.dagger:hilt-compiler:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Paging
    implementation("androidx.paging:paging-runtime-ktx:3.3.0")

    // GMS
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // Other
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.squareup.wire:wire-runtime:4.9.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.test.espresso:espresso-contrib:$espresso")
    implementation("androidx.test.espresso:espresso-idling-resource:$espresso")
    implementation("com.google.guava:guava:31.0.1-android")
    debugImplementation("androidx.fragment:fragment-testing:1.7.1")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("org.mockito:mockito-core:4.4.0")
    testImplementation("org.mockito:mockito-inline:4.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1-Beta")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("com.jraska.livedata:testing-ktx:1.3.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestUtil("androidx.test:orchestrator:1.4.2")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1-Beta")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espresso")
    androidTestImplementation("androidx.test.espresso:espresso-intents:$espresso")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.44.2")
    androidTestImplementation("androidx.work:work-testing:2.9.0")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.44.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
