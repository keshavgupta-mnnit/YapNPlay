package com.kglabs28.yapnplay.appengine.appmanager

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * The `update_config` Remote Config value, as JSON:
 * `{"minVersion": "1.2.0", "latestVersion": "1.3.0", "message": "…"}`.
 * Below [minVersion] the app must update (it cannot be used); below
 * [latestVersion] it may (the player can dismiss it). Versions are
 * `major.minor.patch`; anything after `-` (e.g. `1.0.0-beta`) is ignored.
 * [message] is optional extra text on the update card.
 */
@Serializable
data class UpdateRule(
    val minVersion: String = "",
    val latestVersion: String = "",
    val message: String = "",
) {
    fun need(currentVersion: String): UpdateNeed = when {
        isOlder(currentVersion, minVersion) -> UpdateNeed.REQUIRED
        isOlder(currentVersion, latestVersion) -> UpdateNeed.OPTIONAL
        else -> UpdateNeed.NONE
    }

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        /** Never throws: an empty or broken value means no update is asked for. */
        fun parse(raw: String): UpdateRule =
            if (raw.isBlank()) UpdateRule() else runCatching { json.decodeFromString(serializer(), raw) }.getOrElse { UpdateRule() }

        /** True when [version] is below [target]; a blank or unreadable [target] never is. */
        fun isOlder(version: String, target: String): Boolean {
            val wanted = parts(target) ?: return false
            val have = parts(version) ?: return false
            for (i in 0 until maxOf(wanted.size, have.size)) {
                val a = have.getOrElse(i) { 0 }
                val b = wanted.getOrElse(i) { 0 }
                if (a != b) return a < b
            }
            return false
        }

        private fun parts(version: String): List<Int>? =
            version.substringBefore('-').trim().takeIf { it.isNotEmpty() }
                ?.split('.')?.map { it.trim().toIntOrNull() ?: return null }
    }
}

enum class UpdateNeed { NONE, OPTIONAL, REQUIRED }
