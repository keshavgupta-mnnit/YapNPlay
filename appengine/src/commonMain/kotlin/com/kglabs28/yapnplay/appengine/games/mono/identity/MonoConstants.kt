package com.kglabs28.yapnplay.appengine.games.mono.identity

import com.kglabs28.yapnplay.appengine.games.base.rules.HouseRuleOption

object MonoConstants {
    const val ID = "mono"
    const val DISPLAY_NAME = "MONO"
    val TAGLINES = listOf("Match", "Play", "Win")

    const val PLAYERS_MIN = 2
    const val PLAYERS_MAX = 8
    const val QUICK_PLAY_SEATS = 4

    const val SEARCH_SECONDS = 30L
    const val TURN_SECONDS = 15L
    const val STARTING_HAND_SIZE = 7
    const val CALL_WINDOW_MS = 2500L

    enum class Color { RED, GREEN, BLUE, GOLD }

    enum class CardKind {
        NUMBER, REVERSE, SKIP, DRAW_TWO,
        PRISM_COLOR_WILD, PRISM_DRAW_FOUR,
    }

    val DECK_SIZE = 108

    /**
     * Room house rules, all on by default. Quick Play always uses the
     * official rules (every option off); see [FIXED_RULES].
     */
    val HOUSE_RULES = listOf(
        HouseRuleOption("no_power_finish", "No Power Finish", "Your last card can't be a power card (anything but a number): draw instead", true),
        HouseRuleOption("call_before_play", "Call Before You Play", "Press MONO on your turn before playing your second-last card", true),
        HouseRuleOption("draw_four_anytime", "+4 Anytime", "Play a Wild +4 even while you hold the colour in play", false),
    )

    val FIXED_RULES = listOf(
        "2-8 players, 7 cards each",
        "Match by colour or by number / symbol: Skip on Skip, Reverse on Reverse, +2 on +2 - or play any Prism card",
        "Skip: the next player misses their turn; Reverse: play turns the other way",
        "Prism Color Wild: choose the next colour",
        "Prism +4: next player draws 4 and loses their turn",
        "Call MONO on your turn while holding 2 cards, before playing",
        "CAUGHT: anyone can catch a player on 1 card who didn't call, until the next player's turn ends",
        "Caught without calling MONO: draw 5. Wrong catch: the one who pressed CAUGHT draws 2",
        "Empty draw pile: the discard pile (all but its top card) is shuffled into a new one",
        "15 seconds per turn; the bot plays when time runs out",
        "Play goes on after the first player finishes; from the 2nd finisher (3rd in rooms of 5+) the table votes to keep playing or end",
        "Ranked by finishing order, then fewest cards",
    )
}
