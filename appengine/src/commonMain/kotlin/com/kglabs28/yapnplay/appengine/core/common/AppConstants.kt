package com.kglabs28.yapnplay.appengine.core.common

/**
 * App-wide constants. Numbers with meaning live here, never inline.
 * Values already declared in games/base contracts are re-exported, not duplicated.
 */
object AppConstants {
    const val APP_VERSION = "1.0.0-phase7"

    /** Length of a room join code. */
    const val ROOM_CODE_LENGTH = 6

    /** Maximum display-name length. */
    const val MAX_NAME_LENGTH = 24

    /** Maximum room-name length. */
    const val MAX_ROOM_NAME_LENGTH = 30

    /** Maximum members in a room. */
    const val MAX_MEMBERS = 10

    /** Match-start countdown, in seconds. */
    const val COUNTDOWN_SECONDS = 10

    /** Content width cap in dp — wider windows centre content instead of stretching. */
    const val CONTENT_MAX_WIDTH_DP = 1100

    /** Window-width thresholds (dp) for [LayoutTier]. */
    const val TIER_M_MIN_WIDTH_DP = 740
    const val TIER_L_MIN_WIDTH_DP = 960
}
