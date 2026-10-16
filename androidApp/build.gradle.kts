plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
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
}

// Robolectric resolves the manifest through the debug manifest merge
// (androidApp/src/debug declares ComponentActivity for tests) plus
// @Config(packageName) on each test class — no generated files needed.
tasks.withType<Test>().configureEach {
    useJUnit()
}
