package com.kglabs28.yapnplay.appengine.core.common

/**
 * App-wide constants. Numbers with meaning live here, never inline.
 * Values already declared in games/base contracts are re-exported, not duplicated.
 */
object AppConstants {

    /** Length of a room join code. */
    const val ROOM_CODE_LENGTH = 6

    /** Lobby game stage: specks drift up the beam / art floats up and down. */
    const val STAGE_PARTICLE_CYCLE_MS = 6_000
    const val STAGE_BOB_CYCLE_MS = 2_400

    /** Dice: roll length, glow breathing, slow tint drift. */
    const val DICE_ROLL_MS = 1_500
    /** The die rests on its result this long before any piece moves. */
    const val LUDO_DICE_REST_MS = 500L
    /** City Bazaar: a token's hop from one tile to the next. */
    const val CB_HOP_MS = 200
    /** City Bazaar: each side column of player pods, as a share of the width. */
    const val CB_SIDE_COLUMN_WEIGHT = 0.21f
    /** The "auto-paying rent" notice: how long it stays (its bar fills meanwhile). */
    const val CB_RENT_NOTICE_MS = 3_000L
    /** Money popups off: how long the amount takes to fly from payer to receiver. */
    const val CB_MONEY_FLIGHT_MS = 1_100L
    /** City Bazaar: how long a balance-sheet toast stays. */
    const val CB_TOAST_MS = 2_500L
    /** A switch the user flipped shows its new value this long while the server confirms. */
    const val TOGGLE_CONFIRM_MS = 5_000L
    /** A tap within this long after the panel closed counts as the tap that closed it. */
    const val PANEL_RETAP_MS = 400L
    /** Google Play Games: the game server (web) OAuth client the backend checks sign-ins with. Public, not a secret. */
    const val PLAY_GAMES_WEB_CLIENT_ID = "532749629825-lflcpgr3pn64o7emjnvfdld8i0inpqhq.apps.googleusercontent.com"
    /** EXP for 1st, 2nd, 3rd and everyone after, until the server hands it out. */
    val PLACE_EXP = listOf(400, 300, 200, 100)
    /** City Bazaar: a balance counting to its new value. */
    const val CB_CASH_COUNT_MS = 900
    /** In the background this long (not a quick glance) and the server is told the user is away. */
    const val LUDO_AWAY_GRACE_MS = 5_000L
    const val DICE_PULSE_MS = 1_800
    const val DICE_TINT_CYCLE_MS = 9_000

    /** Ludo: one square hop, a captured piece sliding home, legal-piece pulse, timer refresh. */
    const val LUDO_HOP_MS = 220
    const val LUDO_SLIDE_MS = 650
    const val LUDO_SHIFT_MS = 160
    const val LUDO_LEGAL_PULSE_MS = 700
    /** Board neon (lanes, bases) breathing in and out. */
    const val LUDO_NEON_BREATH_MS = 1_600
    const val LUDO_TIMER_TICK_MS = 100L

    /** Ludo table: width share of each player-card column, talking glow blink. */
    const val LUDO_CARD_COLUMN_WEIGHT = 0.2f
    /** Board width share: it sits right of the user's card, before the opponents. */
    const val LUDO_BOARD_WIDTH_WEIGHT = 0.68f
    /** Spectating: a column of cards on each side, the board centred between them. */
    const val LUDO_SPECTATOR_BOARD_WIDTH_WEIGHT = 0.6f
    /** The exit dialog closes by itself after this long without a choice. */
    const val EXIT_DIALOG_SECONDS = 30L

    /** Room-code caret blink (half period). */
    const val CARET_BLINK_MS = 500

    /** Notifications kept in the top bar bell's history. */
    const val NOTIFICATION_HISTORY_MAX = 10

    /** Unread badge shows "9+" above this. */
    const val NOTIFICATION_BADGE_MAX = 9

    /** Maximum display-name length. */
    const val MAX_NAME_LENGTH = 24

    /** Maximum room-name length. */
    const val MAX_ROOM_NAME_LENGTH = 30

    /** Maximum members in a room. */
    const val MAX_MEMBERS = 10

    /**
     * The hosted backend: the built-in default for AppManager's
     * `server_base_url` Remote Config key, so moving servers (e.g. Render →
     * Oracle) needs no app release. No screen or repository hardcodes a URL.
     */
    const val SERVER_BASE_URL = "https://yapnplay-backend.onrender.com"

    /**
     * Invite links: https://INVITE_HOST/join/<code>. Must match the
     * `inviteHost` manifest placeholder in androidApp/build.gradle.kts, and
     * the server must host /.well-known/assetlinks.json on this domain so
     * Android opens the app instead of the browser (App Links).
     */
    const val INVITE_HOST = "kglabs28-yapnplay.web.app"

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
    const val ROOMS_LIST_PANEL_WEIGHT = 0.57f
    const val ROOMS_FRIENDS_PANEL_WEIGHT = 0.43f
    // Create Room / Join are as tall as Your Rooms / Online Friends beside them.
    const val ROOMS_CREATE_CARD_WEIGHT = ROOMS_LIST_PANEL_WEIGHT
    const val ROOMS_JOIN_CARD_WEIGHT = ROOMS_FRIENDS_PANEL_WEIGHT

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
    const val LOBBY_MEMBERS_WEIGHT = 0.34f
    const val LOBBY_GAME_WEIGHT = 0.36f
    const val LOBBY_RULES_WEIGHT = 0.34f

    /** Lobby phase 1: SELECT GAME width as a share of the picker. */
    const val LOBBY_SELECT_WIDTH_FRACTION = 0.5f

    /** START/READY sits under the member list, 10% shorter on each side. */

    /** Wave animation cycle while someone talks. */
    const val VOICE_WAVE_CYCLE_MS = 700

    /** A room match needs at least this many humans ready (plus bots if allowed). */
    const val LOBBY_MIN_HUMANS_TO_START = 2

    /** Friends the online-friends arrows scroll by per tap. */
    const val FRIENDS_SCROLL_STEP = 3

    /** A user's ID that friends type to add them: 6 characters, no look-alikes (0/O, 1/I). */
    const val USER_CODE_LENGTH = 6
    const val USER_CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    /** Usernames (not unique): this long, from these characters. */
    const val MIN_USERNAME_LENGTH = 3
    const val MAX_USERNAME_LENGTH = 20
    const val USERNAME_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_."
    /** Profile avatar picker: avatars per row. */
    const val AVATAR_PICKER_COLUMNS = 6
    /** How long "Copied" shows after copying the user's ID. */
    const val PROFILE_COPIED_MS = 1_500L

    /** Remote Config re-fetch gap: changed values reach players within an hour. */
    const val REMOTE_CONFIG_FETCH_SECONDS = 3_600L
}
