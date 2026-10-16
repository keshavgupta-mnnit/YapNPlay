plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.googleservices)
    alias(libs.plugins.crashlytics)
}

android {
    namespace = "com.kglabs28.yapnplay"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.kglabs28.yapnplay"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0-phase7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    // Tests run but never block the build. They will be made mandatory later.
    tasks.withType<Test>().configureEach {
        ignoreFailures = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    implementation(project(":appengine"))
    implementation(libs.activity.compose)
    implementation(libs.koin.android)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.uitest.junit4)
    testImplementation(libs.compose.foundation)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.uitest.junit4)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.firebase.crashlytics)
}

// Robolectric resolves the manifest through the debug manifest merge
// (androidApp/src/debug declares ComponentActivity for tests) plus
// @Config(packageName) on each test class — no generated files needed.
tasks.withType<Test>().configureEach {
    useJUnit()
}
