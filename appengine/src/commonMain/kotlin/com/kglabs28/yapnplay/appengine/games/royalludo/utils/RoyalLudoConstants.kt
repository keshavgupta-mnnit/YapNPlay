package com.kglabs28.yapnplay.appengine.games.royalludo.utils

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
