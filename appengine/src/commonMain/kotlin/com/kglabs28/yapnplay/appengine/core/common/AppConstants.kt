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

    /**
     * Dev backend base URL (emulator loopback). The Retrofit client takes
     * this as its default; flavors override it later. No screen or
     * repository hardcodes a URL — they all read this.
     */
    const val SERVER_BASE_URL = "http://10.0.2.2:8080"

    /** Window-width thresholds (dp) for [LayoutTier]. */
    const val TIER_M_MIN_WIDTH_DP = 740
    const val TIER_L_MIN_WIDTH_DP = 960

    /**
     * Dashboard tab frame: every tab renders inside 80% of the width
     * with 10% margins on the left, right and bottom, over the home
     * background. One constant frame for Home, Games and Rooms.
     */
    const val TAB_CONTENT_WIDTH_FRACTION = 0.8f
    const val TAB_CONTENT_HEIGHT_FRACTION = 0.9f

    /**
     * Top bar free-space split: the gap before the tab strip is twice
     * the gap after it, so tabs sit near the centre and the balances
     * and buttons hug the right edge.
     */
    const val TOP_BAR_LEADING_GAP_WEIGHT = 2f
    const val TOP_BAR_TRAILING_GAP_WEIGHT = 1f
}
