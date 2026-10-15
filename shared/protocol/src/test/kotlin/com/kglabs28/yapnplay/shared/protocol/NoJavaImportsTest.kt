package com.kglabs28.yapnplay.shared.protocol

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertFalse

/**
 * Verifies that the shared protocol module imports nothing from java.* or android.*,
 * so it can become Kotlin Multiplatform later.
 *
 * Note: The test file itself uses java.io.File to scan sources — that is acceptable
 * because test code is not part of the shared library's public API and is not
 * included in KMP builds. The main source set must remain clean.
 */
class NoJavaImportsTest {

    @Test
    fun `protocol main source imports nothing from java or android`() {
        val sourceDir = File("src/main/kotlin")
        val kotlinFiles = sourceDir.walkTopDown().filter { it.extension == "kt" }

        for (file in kotlinFiles) {
            val lines = file.readLines()
            for ((index, line) in lines.withIndex()) {
                val trimmed = line.trim()
                assertFalse(
                    trimmed.startsWith("import java.") || trimmed.startsWith("import javax."),
                    "${file.path}:${index + 1} imports java.* — not KMP-safe"
                )
                assertFalse(
                    trimmed.startsWith("import android."),
                    "${file.path}:${index + 1} imports android.* — not KMP-safe"
                )
            }
        }
    }
}
