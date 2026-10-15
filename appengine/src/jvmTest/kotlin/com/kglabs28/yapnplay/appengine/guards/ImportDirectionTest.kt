package com.kglabs28.yapnplay.appengine.guards

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

// Build-enforced replacement for module boundaries: with a single library,
// folders must obey the one-way dependency rule or this test fails.
// Allowed direction only, never a cycle:
// a game folder depends on games/common, which depends on games/base.
// protocol depends on games/common, which depends on games/base.
// Note: no block comments in this file. Kotlin block comments nest, so a
// stray opener inside one would swallow the rest of the file.
class ImportDirectionTest {

    private data class FolderRule(val folder: String, val forbidden: List<String>)

    private val base = "com.kglabs28.yapnplay.appengine"

    private val rules = listOf(
        FolderRule(
            "games/base",
            listOf("$base.games.common", "$base.games.royalludo", "$base.protocol"),
        ),
        FolderRule(
            "games/common",
            listOf("$base.games.royalludo", "$base.protocol"),
        ),
        FolderRule("games/royalludo", listOf("$base.protocol")),
        FolderRule("protocol", listOf("$base.games.royalludo")),
    )

    @Test
    fun checkDirection() {
        val root = File("src/commonMain/kotlin/com/kglabs28/yapnplay/appengine")
        assertTrue(root.isDirectory, "Run from the appengine module directory")
        val violations = mutableListOf<String>()
        val sources = root.walkTopDown().filter { it.extension == "kt" }.toList()
        for (file in sources) {
            val relative = file.relativeTo(root).path.replace(File.separatorChar, '/')
            val rule = rules.firstOrNull { candidate -> relative.startsWith(candidate.folder + "/") }
                ?: continue
            file.readLines().forEachIndexed { index, line ->
                val trimmed = line.trim()
                if (!trimmed.startsWith("import ")) return@forEachIndexed
                val imported = trimmed.removePrefix("import ").substringBefore(" as ")
                if (rule.forbidden.any { prefix -> imported.startsWith(prefix) }) {
                    violations.add(file.name + ":" + (index + 1) + ": " + imported)
                }
            }
        }
        assertTrue(violations.isEmpty(), "Bad imports:\n" + violations.joinToString("\n"))
    }
}
