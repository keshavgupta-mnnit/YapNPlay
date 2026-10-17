package com.kglabs28.yapnplay.appengine.games.royalludo.utils

import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRuleOption

// Identity and showcase (shared with the server's game list).
const val ROYAL_LUDO_ID = "royal-ludo"
const val ROYAL_LUDO_DISPLAY_NAME = "Royal Ludo"
val ROYAL_LUDO_TAGLINES = listOf("Roll", "Race", "Rule")

/**
 * Every Royal Ludo table has exactly 4 players (min = max). Rooms with
 * fewer friends use the leader's bot fill. The engine itself still runs
 * 2–4 seats, so this is a product rule, not an engine limit.
 */
const val ROYAL_LUDO_PLAYERS = 4

/** A random (Quick Play) Royal Ludo match fills all four seats. */
const val ROYAL_LUDO_QUICK_PLAY_SEATS = ROYAL_LUDO_PLAYERS

/** Quick Play search window before empty seats fill with CPU players. */
const val ROYAL_LUDO_SEARCH_SECONDS = 30L

const val TRACK_SIZE = 52
const val PROGRESS_IN_BASE = -1
const val PROGRESS_FINISHED = 56
const val PROGRESS_FINISH = 56
const val HOME_COLUMN_LENGTH = 5
const val PROGRESS_ENTER_HOME = 50
const val RED_START = 0
const val GREEN_START = 13
const val YELLOW_START = 26
const val BLUE_START = 39
val STAR_SQUARES = setOf(8, 21, 34, 47)
const val PIECES_PER_COLOUR = 4
const val MAX_CONSECUTIVE_SIXES = 3
const val SCORE_FINISH = 1000
const val SCORE_CAPTURE = 800
const val SCORE_LEAVE_BASE = 600
const val SCORE_SAFE = 400

// Lobby: house-rule toggles (keys match RoyalLudoHouseRules fields) and
// the fixed rule sheet. Defaults are the traditional rules.
val ROYAL_LUDO_HOUSE_RULES = listOf(
    HouseRuleOption("extra_turn_on_six", "Extra Turn on 6", "Roll again after a six", true),
    HouseRuleOption("extra_turn_on_capture", "Extra Turn on Capture", "Roll again after sending a piece home", true),
    HouseRuleOption("extra_turn_on_home", "Extra Turn on Finish", "Roll again when a piece reaches home", true),
    HouseRuleOption("three_sixes_forfeit", "Three Sixes Forfeit", "A third six in a row loses the turn", true),
    HouseRuleOption("pair_protection", "Pair Protection", "Two pieces together can't be captured", true),
    HouseRuleOption("auto_move_single_option", "Auto Move", "Move automatically when only one move exists", false),
)

val ROYAL_LUDO_FIXED_RULES = listOf(
    "4 players, 4 pieces each",
    "A piece leaves base only on a 6",
    "Exact roll needed to reach home",
    "Landing on a lone opponent sends it back to base",
    "Start squares and star squares are safe",
    "30 seconds per turn; auto-play when time runs out",
    "Away after 3 missed turns or 60 s offline: the seat is removed",
)
