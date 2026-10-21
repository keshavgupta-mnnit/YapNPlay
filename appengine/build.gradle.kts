plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

// Single appengine library. Everything below is a plain source folder,
// not a Gradle module: core/, games/base/, games/common/, games/*,
// protocol/, ui/, di/, navigation/.
kotlin {
    android {
        namespace = "com.kglabs28.yapnplay.appengine"
        compileSdk = 37
        minSdk = 26
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
        // Android resources (res/font) are opt-in with the KMP library plugin.
        androidResources {
            enable = true
        }
        withHostTest {}
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
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            // Bundled images (commonMain/composeResources), every platform.
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)
            implementation(libs.navigation.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.ktor.client.core)
            // Images from AppManager URLs (backgrounds, game art), any platform.
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.turbine)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
        }
        named("androidHostTest") {
            dependencies {
                implementation(libs.junit)
                implementation(libs.robolectric)
                implementation(libs.compose.uitest.junit4)
            }
        }
        androidMain.dependencies {
            // Explicit versions (from BOM 34.0.0): Kotlin's deprecated
            // platform() cannot be used inside KMP source-set blocks.
            implementation(libs.koin.android)
            // BackHandler for the blocking overlay (BlockBackPress actual).
            implementation(libs.activity.compose)
            implementation(libs.firebase.config)
            implementation(libs.firebase.crashlytics)
            implementation(libs.kotlinx.coroutines.play.services)
            // Live voice: joins the LiveKit room the backend issues a token for.
            implementation(libs.livekit.android)
            implementation(libs.play.app.update)
            // Google Play Games sign-in (Clash of Clans style account restore).
            implementation(libs.play.games.v2)
            // Rewarded ads (coins, lifting a play block).
            implementation(libs.play.services.ads)
        }
    }
}

// Generated accessor for commonMain/composeResources: Res.drawable.<file>.
compose.resources {
    packageOfResClass = "com.kglabs28.yapnplay.appengine.resources"
}

// Tests run but never block the build. They will be made mandatory later.
tasks.withType<Test>().configureEach {
    ignoreFailures = true
}

// Guideline-enforcement tests (folder/import/source guards) live OUTSIDE
// the build path on purpose: `./gradlew build` must never fail because of
// a style rule. Run `./gradlew :appengine:guidelineCheck` before pushing.
tasks.named<Test>("jvmTest") {
    filter {
        exclude("com.kglabs28.yapnplay.appengine.guards.*")
    }
}

tasks.register("guidelineCheck", Test::class) {
    group = "verification"
    description = "Runs guideline-enforcement tests (excluded from build)."
    val jvmTest = tasks.named<Test>("jvmTest")
    testClassesDirs = files(jvmTest.map { it.testClassesDirs })
    classpath = files(jvmTest.map { it.classpath })
    filter {
        include("com.kglabs28.yapnplay.appengine.guards.*")
    }
}
