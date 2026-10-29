package com.kglabs28.yapnplay.appengine.appmanager

/**
 * When the app asks for an update, from AppManager: below [minVersion]
 * it must update (it cannot be used); otherwise, with [promptEnabled]
 * and Google Play holding a newer build, it may (the player can close
 * it). Versions are `major.minor.patch`; anything after `-` (e.g.
 * `1.0.0-beta`) is ignored. [message] is optional extra text on the card.
 */
data class UpdateRule(
    val promptEnabled: Boolean = true,
    val minVersion: String = "",
    val message: String = "",
) {
    fun need(currentVersion: String, storeHasUpdate: Boolean): UpdateNeed = when {
        isOlder(currentVersion, minVersion) -> UpdateNeed.REQUIRED
        promptEnabled && storeHasUpdate -> UpdateNeed.OPTIONAL
        else -> UpdateNeed.NONE
    }

    companion object {
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
