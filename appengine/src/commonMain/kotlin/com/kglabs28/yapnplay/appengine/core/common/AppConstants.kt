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

    /**
     * Invite links: https://INVITE_HOST/join/<code>. Must match the
     * `inviteHost` manifest placeholder in androidApp/build.gradle.kts, and
     * the server must host /.well-known/assetlinks.json on this domain so
     * Android opens the app instead of the browser (App Links).
     */
    const val INVITE_HOST = "yapnplay.app"

    /** App-only links (notification taps): yapnplay://room/<roomId>. Matches `appScheme`. */
    const val APP_SCHEME = "yapnplay"

    /** Window-width thresholds (dp) for [LayoutTier]. */
    const val TIER_M_MIN_WIDTH_DP = 740
    const val TIER_L_MIN_WIDTH_DP = 960


    /**
     * Top bar free-space split: the gap before the tab strip is twice
     * the gap after it, so tabs sit near the centre and the balances
     * and buttons hug the right edge.
     */
    const val TOP_BAR_LEADING_GAP_WEIGHT = 2f
    const val TOP_BAR_TRAILING_GAP_WEIGHT = 1f

    /**
     * Home card art is drawn with ~10% transparent margin on every side;
     * zooming by this much makes the art itself fill its slot.
     */
    const val CARD_ART_ZOOM = 1.2f

    /** Home QUICK PLAY button width as a share of its card. */
    const val HOME_CTA_WIDTH_FRACTION = 0.65f

    /**
     * Rooms tab split. Columns are 60:40 (create/join left, lists right);
     * inside each column the cards keep the design's height ratio.
     */
    const val ROOMS_LEFT_COLUMN_WEIGHT = 0.6f
    const val ROOMS_RIGHT_COLUMN_WEIGHT = 0.4f
    const val ROOMS_CREATE_CARD_WEIGHT = 0.64f
    const val ROOMS_JOIN_CARD_WEIGHT = 0.36f
    const val ROOMS_LIST_PANEL_WEIGHT = 0.57f
    const val ROOMS_FRIENDS_PANEL_WEIGHT = 0.43f

    /**
     * Game carousel: the focused game takes this share of the width; the
     * neighbours peek in at either side, smaller and dimmed.
     */
    const val CAROUSEL_PAGE_WIDTH_FRACTION = 0.34f
    const val CAROUSEL_SIDE_SCALE = 0.85f
    const val CAROUSEL_SIDE_ALPHA = 0.85f

    /**
     * The carousel loops: it is a very long strip of pages that repeat
     * the games, started in the middle, so it never reaches an end.
     */
    const val CAROUSEL_LOOP_PAGES = 10_000

    /** Neighbour pages composed on each side (so they are always drawn). */
    const val CAROUSEL_PAGES_BEYOND_VIEW = 2

    /** Game grid columns (Grid View). */
    const val GAME_GRID_COLUMNS = 6

    /** Game Zone QUICK PLAY button width as a share of the screen. */
    const val GAME_ZONE_CTA_WIDTH_FRACTION = 0.36f

    /** How long "Match found!" shows before the match screen opens. */
    const val MATCH_FOUND_HOLD_MS = 1_200L

    /** Countdown refresh while searching for players. */
    const val SEARCH_TICK_MS = 1_000L

    /** Lobby columns: member list | game | house rules (weights). */
    const val LOBBY_MEMBERS_WEIGHT = 0.3f
    const val LOBBY_GAME_WEIGHT = 0.42f
    const val LOBBY_RULES_WEIGHT = 0.28f

    /** Lobby phase 1: SELECT GAME width as a share of the picker. */
    const val LOBBY_SELECT_WIDTH_FRACTION = 0.5f

    /** Lobby bottom bar: the Start/Ready button share of the width. */
    const val LOBBY_PRIMARY_WEIGHT = 0.3f

    /** Bars in the "talking" wave next to a member name. */
    const val VOICE_WAVE_BARS = 12

    /** Wave animation cycle while someone talks. */
    const val VOICE_WAVE_CYCLE_MS = 700

    /** A room match needs at least this many humans ready (plus bots if allowed). */
    const val LOBBY_MIN_HUMANS_TO_START = 2

    /** Friends the online-friends arrows scroll by per tap. */
    const val FRIENDS_SCROLL_STEP = 3
}
