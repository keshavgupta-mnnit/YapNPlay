package com.kglabs28.yapnplay.appengine.features.playrooms.domain

/**
 * PlayRoom badge picked when the room is created. The UI maps each kind to
 * its glyph and color; unknown values from the server fall back to [GROUP].
 */
enum class PlayRoomIcon {
    GROUP, DICE, FAMILY, GAMEPAD;

    /** The server's key for this icon ("dice"). */
    val key: String get() = name.lowercase()

    companion object {
        /** Server key ("dice") to icon; unknown or missing → [GROUP]. */
        fun fromKey(key: String?): PlayRoomIcon =
            entries.firstOrNull { it.name.equals(key, ignoreCase = true) } ?: GROUP
    }
}
