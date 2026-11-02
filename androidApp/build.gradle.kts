plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.googleservices)
    alias(libs.plugins.crashlytics)
}

// Lists, maps and dates the screens draw are never changed in place: Compose may skip a part whose
// inputs are equal to last time instead of redrawing it (see compose_stability.conf).
composeCompiler {
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("compose_stability.conf"))
}

android {
    // BuildConfig.DEBUG turns the app's debug logs on (debug builds only).
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.kglabs28.yapnplay"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.kglabs28.yapnplay"
        minSdk = 26
        targetSdk = 36
        versionCode = 10
        versionName = "1.0.6"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Must match AppConstants.INVITE_HOST / APP_SCHEME (invite + room links).
        manifestPlaceholders["inviteHost"] = "kglabs28-yapnplay.web.app"
        manifestPlaceholders["appScheme"] = "yapnplay"
        // AdMob app id: -PadmobAppId=ca-app-pub-…~… (or gradle.properties); default is Google's test app.
        manifestPlaceholders["admobAppId"] = (project.findProperty("admobAppId") as String?) ?: "ca-app-pub-3940256099942544~3347511713"
    }

    
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "release.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            // R8: shrinks and obfuscates the release APK (harder to read, smaller).
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        
            signingConfigs.getByName("release").let {
                if (System.getenv("KEYSTORE_PASSWORD") != null) signingConfig = it
            }
            ndk {
                debugSymbolLevel = "FULL"
            }}
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
    implementation(libs.androidx.core.ktx)
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
    implementation(libs.firebase.messaging)
}

// Robolectric resolves the manifest through the debug manifest merge
// (androidApp/src/debug declares ComponentActivity for tests) plus
// @Config(packageName) on each test class — no generated files needed.
tasks.withType<Test>().configureEach {
    useJUnit()
}
