import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

// The web app: a thin launch shell (the page + its entry point) around :appengine.
// `./gradlew :webApp:wasmJsBrowserDistribution` builds the site into
// webApp/build/dist/wasmJs/productionExecutable (static files for any host).
kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("yapnplay")
        browser {
            commonWebpackConfig {
                outputFileName = "yapnplay.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(project(":appengine"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.browser)
        }
    }
}
