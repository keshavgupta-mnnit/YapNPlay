plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatform.library)
}

kotlin {
    // Android target — uses the KMP Android library plugin.
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    // JVM target — for server and desktop compatibility.
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    // Use the foojay resolver to auto-provision JDK 21 if not installed locally.
    jvmToolchain(21)

    sourceSets {
        commonMain.dependencies {
            // Pure Kotlin — no platform-specific dependencies.
            // kotlinx.datetime is KMP-ready when needed.
        }
        commonTest.dependencies {
            implementation(libs.junit.jupiter.api)
            implementation(libs.junit.jupiter.params)
            runtimeOnly(libs.junit.jupiter.engine)
        }
    }
}

android {
    namespace = "com.kglabs28.yapnplay.shared.game.api"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
