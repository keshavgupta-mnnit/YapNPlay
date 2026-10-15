import org.gradle.api.tasks.WriteProperties

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

// AGP 9 no longer generates test_config.properties (verified: Robolectric
// falls back to package org.robolectric.default and resolves nothing), so
// generate the file Robolectric 4.15 actually reads, with the built-in
// WriteProperties task (config-cache safe, no script code in actions).
// Same applicationId as defaultConfig.
val generateTestConfigProperties = tasks.register("generateTestConfigProperties", WriteProperties::class) {
    group = "verification"
    description = "Generates the test_config.properties Robolectric reads from the test classpath."
    dependsOn("processDebugManifest", "mergeDebugResources")
    val intermediates = layout.buildDirectory.dir("intermediates")
    destinationFile = layout.buildDirectory.file("generated/robolectric-resources/com/android/tools/test_config.properties")
    property(
        "android_merged_manifest",
        intermediates.map { it.file("merged_manifests/debug/processDebugManifest/AndroidManifest.xml").asFile.invariantSeparatorsPath },
    )
    property(
        "android_merged_resources",
        intermediates.map { it.dir("merged_res/debug").asFile.invariantSeparatorsPath },
    )
    property("android_merged_assets", "")
    property("android_custom_package", "com.kglabs28.yapnplay")
}

android {
    // Wires the generated file above onto the unit-test runtime classpath.
    // Plain File (not a Provider): AGP 9 rejects providers in source sets.
    sourceSets {
        getByName("test").resources.srcDir(
            layout.buildDirectory.dir("generated/robolectric-resources").get().asFile,
        )
    }
}

tasks.withType<Test>().configureEach {
    useJUnit()
}

tasks.matching { it.name.endsWith("UnitTestJavaRes") }.configureEach {
    dependsOn(generateTestConfigProperties)
}
