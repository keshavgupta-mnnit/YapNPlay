package com.kglabs28.yapnplay.appengine.core.ui

import com.kglabs28.yapnplay.appengine.ui.components.moderation.GameRuleUi

/** Doodle King: table, tools, guesses, feed, word pick and reveal. */
object DoodleStrings {
    /** The rules shown from the settings menu. */
    val dkRules: List<GameRuleUi> = listOf(
        GameRuleUi("Goal", "Score the most points over 3 rounds, by guessing fast and by drawing well."),
        GameRuleUi("Rounds", "Everyone draws once a round: easy words in round 1, medium in round 2, hard in round 3."),
        GameRuleUi("Your turn to draw", "Pick 1 of 3 words, then draw it before the time runs out. You can't talk on your own turn."),
        GameRuleUi("Guessing", "Type your guess. The first to get it scores the most, then less for each one after."),
        GameRuleUi("Drawer's points", "The drawer scores for every right guess, and a bonus when everyone gets it. Nobody guessing costs nothing."),
        GameRuleUi("Hints", "Letters of the word show up one by one near the end."),
        GameRuleUi("Points by level", "Medium words score 1.5×, hard words 2×, for guessing and for drawing."),
        GameRuleUi("Pass", "Passing, or drawing nothing at all, costs 50 points."),
        GameRuleUi("Keep playing", "When the rounds are over the table votes whether to play another round."),
        GameRuleUi(TableStrings.endEarlyRuleTitle, TableStrings.endEarlyRule(dkEndRanking)),
    )

    /** How Doodle King ranks players when it ends early. */
    const val dkEndRanking = "The most points wins."

    const val dkTitle = "DOODLE KING"
    fun dkSubtitle(mode: String, round: Int, rounds: Int, level: String) = "$mode • Round $round / $rounds • ${levelName(level)}"

    fun levelName(level: String) = when (level) {
        "EASY" -> "Easy"
        "MEDIUM" -> "Medium"
        else -> "Hard"
    }

    // Status line over the canvas.
    fun dkTurnOf(name: String) = "$name's turn to draw"
    const val dkYourTurn = "Your turn to draw"
    fun dkPicking(name: String) = "$name is picking a word…"
    const val dkYouPick = "Pick a word to draw"
    fun dkDrawing(name: String) = "$name is drawing"
    const val dkYouDraw = "You are drawing"
    const val dkWordWas = "The word was"
    const val dkGameOver = "Game over"

    // Tools and buttons.
    const val dkBrush = "Brush"
    const val dkEraser = "Eraser"
    const val dkUndo = "Undo"
    const val dkClear = "Clear all"
    const val dkPass = "PASS"
    const val dkEnter = "ENTER"
    const val dkGuessHint = "Type your guess"
    const val dkChatHint = "Chat with those who guessed"
    const val dkMicLocked = "Your mic is off while you draw"
    val dkInkNames = listOf("Black", "Red", "Blue", "Green", "Yellow")

    // Card tags.
    const val dkYou = "You"
    const val dkDrawingTag = "Drawing"
    const val dkGuessedTag = "Guessed ✓"
    const val dkLeftTag = "Left"

    // Panels.
    const val dkGuesses = "Guesses"
    const val dkLeaderboard = "Leaderboard"
    const val dkNoGuesses = "Guesses show up here"

    // Feed lines (the name is drawn in front).
    const val dkGuessedIt = "guessed it!"
    fun dkVeryClose(guess: String) = "\"$guess\" is very close!"
    fun dkPassed(points: Int) = "passed ($points)"
    fun dkDrewNothing(points: Int) = "drew nothing ($points)"
    const val dkSkipped = "is away: turn skipped"
    const val dkNobodyGot = "Nobody got it"

    // Word pick.
    const val dkPickTitle = "Pick a word"
    fun dkPickSeconds(seconds: Long) = "Picked for you in $seconds s"

    // Reveal and leaderboard.
    fun dkPoints(points: Int) = if (points > 0) "+$points" else points.toString()
    fun dkScore(points: Int) = "$points pts"
    val dkPlaceTitles = listOf("Doodle King", "Royal Artist", "Sharp Eye", "Quick Guess", "Sketcher", "Doodler", "Scribbler", "Newbie")
}
