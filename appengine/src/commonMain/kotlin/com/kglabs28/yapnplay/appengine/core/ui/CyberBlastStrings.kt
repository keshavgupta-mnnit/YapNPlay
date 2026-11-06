package com.kglabs28.yapnplay.appengine.core.ui

import com.kglabs28.yapnplay.appengine.ui.components.moderation.GameRuleUi

/** Cyber Blast: table, controls, countdown, board labels and rules. */
object CyberBlastStrings {
    val blastRules: List<GameRuleUi> = listOf(
        GameRuleUi("Goal", "Be the last player with hearts left."),
        GameRuleUi("Controls", "Steer with the round pad on the left, drop a bomb with the button on the right."),
        GameRuleUi("Bombs", "A bomb goes off after a moment. Its fire runs along the rows and columns; metal pillars stop it."),
        GameRuleUi("Blockers", "Glass blockers break in a blast: +10 each. They stop the fire too."),
        GameRuleUi("Hits", "Hit someone: +100 and your blast reaches one tile further. Hit by someone: −25. Your own bomb: −15."),
        GameRuleUi("Hearts", "Everyone has 5 hearts. After a hit you come back somewhere safe and can't be hurt for a moment."),
        GameRuleUi("Speed boots", "Boots show up now and then. Grab them for 30 seconds of extra speed."),
        GameRuleUi("Closing arena", "After a few minutes the walls close in from the outside. A wall landing on you costs a heart."),
        GameRuleUi(TableStrings.endEarlyRuleTitle, TableStrings.endEarlyRule(blastEndRanking)),
    )

    const val blastEndRanking = "Players still in rank first, then the most points."

    const val blastTitle = "CYBER BLAST"
    fun blastSubtitle(mode: String) = "$mode • Arena Royale"

    const val blastGo = "GO!"
    const val blastYouAreOut = "You're out — watch the rest!"
    fun blastClosingIn(seconds: Long) = "Arena closes in ${seconds}s"
    const val blastDropBomb = "DROP\nBOMB"
    const val blastDropBombLabel = "Drop bomb"
    const val blastPadLabel = "Move"
    const val blastBootsTag = "+30s"
    fun blastPoints(points: Int) = if (points > 0) "+$points" else "$points"
    fun blastScore(score: Int) = "$score pts"
    const val blastOutTag = "Out"
    const val blastLeftTag = "Left"
    const val blastYou = "You"

    val blastPlaceTitles = listOf("Last One Standing", "Runner-up", "Survivor", "Brave", "Fighter", "Blaster", "Rookie", "Spark")
}
