package com.kglabs28.yapnplay.appengine.games.doodleking.identity

import com.kglabs28.yapnplay.appengine.games.base.identity.GameIds
import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRuleOption

/** Doodle King's fixed facts (the server's are the same; it owns the rules). */
object DoodleKingConstants {
    const val ID = GameIds.DOODLE_KING
    const val DISPLAY_NAME = "Doodle King"
    val TAGLINES = listOf("Draw", "Guess", "Rule")

    const val PLAYERS_MIN = 3
    const val PLAYERS_MAX = 8
    const val QUICK_PLAY_SEATS = 4
    const val SEARCH_SECONDS = 30L
    const val DRAW_SECONDS = 60L

    val HOUSE_RULES = listOf(
        HouseRuleOption("hinglish_words", "Hinglish Words", "Words like chai, patang and gol gappa join the English ones", true),
    )

    val FIXED_RULES = listOf(
        "Rooms 3–8 players, random tables 4",
        "Everyone draws once a round: easy words, then medium, then hard",
        "The drawer picks 1 of 3 words and can't talk all turn",
        "Guess first for the most points; the drawer scores for every right guess",
        "Letters show up as hints near the end",
        "Pass, or draw nothing: −50",
    )

    /** Points on the canvas run 0–4095 on both axes; the canvas is 4:3. */
    const val CANVAS_MAX = 4_095
    const val CANVAS_ASPECT = 4f / 3f

    /** A drawn line goes out at most this often (ms), in one part. */
    const val STROKE_SEND_MS = 60L

    /** A guess is cut to this many characters (the server's limit). */
    const val MAX_GUESS_LENGTH = 40
}
