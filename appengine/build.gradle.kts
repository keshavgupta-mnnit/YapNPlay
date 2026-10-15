plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
}

// Single app-engine library. Everything below is a plain source folder,
// not a Gradle module: core/, games-api/, protocol/, games/*.
// Each folder contains only the architecture layers it needs
// (engine/domain/data/ui/utils) — nothing more.
kotlin {
    android {
        namespace = "com.kglabs28.yapnplay.appengine"
        compileSdk = 37
        minSdk = 24
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
