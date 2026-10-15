plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    // game-api is a pure Kotlin library — no Android, no java.* imports.
    // It stays KMP-friendly so it can be shared across platforms later.

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
}
