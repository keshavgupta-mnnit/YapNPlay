package com.kglabs28.yapnplay.appengine.guards

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Source guards: raw dp/sp numbers live only in core/designsystem, and
 * user-visible strings never appear inline in UI code (use Strings.kt).
 */
class SourceGuardTest {

    private fun mainSources(): List<File> {
        val root = File("src/commonMain/kotlin/com/kglabs28/yapnplay/appengine")
        assertTrue(root.isDirectory, "Run from the appengine module directory")
        return root.walkTopDown().filter { it.extension == "kt" }.toList()
    }

    private fun relative(file: File): String {
        val root = File("src/commonMain/kotlin/com/kglabs28/yapnplay/appengine")
        return file.relativeTo(root).path.replace(File.separatorChar, '/')
    }

    @Test
    fun `no raw dp or sp outside designsystem`() {
        val pattern = Regex("""\b\d+(\.\d+)?\.(dp|sp)\b""")
        val violations = mainSources()
            .filter { !relative(it).startsWith("core/designsystem/") }
            .flatMap { file ->
                file.readLines().mapIndexedNotNull { index, line ->
                    if (pattern.containsMatchIn(line)) "${file.name}:${index + 1}: ${line.trim()}" else null
                }
            }
        assertTrue(
            violations.isEmpty(),
            "Raw dp/sp outside core/designsystem:\n" + violations.joinToString("\n"),
        )
    }

    @Test
    fun `no logic inside composable files`() {
        // A @Composable file owns UI only. Every top-level fun must be a
        // @Composable; helpers, validation and formatting live in util
        // classes, extensions in their own files, or use cases.
        val uiPath = Regex("^(ui/|features/[^/]+/ui/|navigation/).*")
        val violations = mainSources()
            .filter { file -> relative(file).matches(uiPath) }
            .flatMap { file ->
                val lines = file.readLines()
                lines.mapIndexedNotNull { index, line ->
                    // Column zero only: local helpers inside a @Composable body
                    // are rendering code, not business logic.
                    if (!line.matches(Regex("^(private |internal )?fun \\w+.*"))) {
                        return@mapIndexedNotNull null
                    }
                    val annotated = lines.subList(0, index).asReversed()
                        .takeWhile { it.isBlank() || it.trimStart().startsWith("@") }
                        .any { it.contains("@Composable") }
                    if (!annotated) {
                        "${file.name}:${index + 1}: ${line.trim()} (non-composable top-level fun)"
                    } else {
                        null
                    }
                }
            }
        assertTrue(
            violations.isEmpty(),
            "Logic inside @Composable files:\n" + violations.joinToString("\n"),
        )
    }

    @Test
    fun `no inline user-visible strings in UI code`() {
        // BasicText/Text with a string literal is banned in ui/, features/
        // and navigation/ — text comes from Strings.kt via the caller.
        val pattern = Regex("""(BasicText|Text)\(\s*text\s*=\s*""")
        val violations = mainSources()
            .filter { file ->
                val path = relative(file)
                path.startsWith("ui/") || path.startsWith("features/") || path.startsWith("navigation/")
            }
            .flatMap { file ->
                file.readLines().mapIndexedNotNull { index, line ->
                    if (pattern.containsMatchIn(line)) "${file.name}:${index + 1}: ${line.trim()}" else null
                }
            }
        assertTrue(
            violations.isEmpty(),
            "Inline strings in UI code (use Strings.kt):\n" + violations.joinToString("\n"),
        )
    }
}
