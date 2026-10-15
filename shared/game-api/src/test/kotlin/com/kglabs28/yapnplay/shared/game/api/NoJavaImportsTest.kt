package com.kglabs28.yapnplay.shared.game.api

import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertFalse

/**
 * Verifies that the shared game-api module imports nothing from java.* or android.*,
 * so it can become Kotlin Multiplatform later.
 */
class NoJavaImportsTest {

    @Test
    fun `game-api imports nothing from java or android`() {
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
