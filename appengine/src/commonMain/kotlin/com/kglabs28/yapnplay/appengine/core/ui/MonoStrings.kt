package com.kglabs28.yapnplay.appengine.core.ui

/**
 * MONO and MONO: Complete Chaos: the table, the cards and the results.
 * Both games share these words; only the title differs.
 */
object MonoStrings {
    const val monoTitle = "MONO"
    const val monoChaosTitle = "MONO: COMPLETE CHAOS"

    /** Printed on the cards: the notch and back wordmark, the skip-everyone mark and the Chaos back plate. */
    const val monoWordmark = "MONO"
    const val monoAll = "ALL"
    const val monoDiscardAll = "+ALL"
    const val monoChaosPlate = "COMPLETE CHAOS"

    const val monoCalled = "MONO!"
    /** The one callout button: calls MONO for the user, or CAUGHT on whoever just went down to one card. */
    const val monoCallButton = "MONO/CAUGHT"
    const val monoDrawButton = "Draw Card"
    fun monoTakeButton(penalty: Int) = "Take +$penalty"
    fun monoSpectatorCount(count: Int) = if (count == 1) "1 Spectator" else "$count Spectators"

    /** Declines the card just drawn and ends the turn. */
    const val monoPass = "PASS"
    fun monoStack(penalty: Int) = "STACK: +$penalty CARDS"


    const val monoYourTurn = "YOUR TURN"
    const val monoOpenFailed = "Could not open this match"

    const val monoChooseColour = "Choose a colour"
    const val monoChooseRouletteColour = "Name the colour your victim must draw to"


    const val monoAway = "Away"
    const val monoAutoplay = "Autoplay"
    const val monoMicShort = "Mic"
    const val monoAutoShort = "Auto"
    const val monoMic = "Microphone"
    const val monoEliminated = "ELIMINATED"

    /** Rule of 7: choose whose hand to trade. */
    const val monoSwapTitle = "Swap hands"
    const val monoSwapBody = "Choose the player you want to trade your whole hand with."


    /** The banner when a player empties their hand. */
    fun monoFinishBanner(name: String, place: String) = "🏆 $name finishes $place!"
    fun monoYouFinishBanner(place: String) = "🏆 You finish $place!"
    const val monoLeaveTitle = "Leave the match?"
    const val monoLeaveBody = "A CPU will play your seat for the rest of the match."
    const val monoStay = "STAY"
    const val monoLeave = "LEAVE"
    const val monoVoteTitle = "Keep playing?"
    fun monoVoteBody(seconds: Long) = "Winners are in. Play on for the next places? ${seconds}s"
    fun monoVoteCount(yes: Int, no: Int, voters: Int) = "$yes keep · $no end · $voters voting"
    const val monoVoteKeep = "KEEP PLAYING"
    const val monoVoteEnd = "END GAME"
    const val monoVoteWaiting = "Waiting for the others…"
    const val monoFinished = "Finished"
    fun monoCardsLeft(cards: Int) = if (cards == 1) "1 card left" else "$cards cards left"
    const val monoKnockedOutScore = "Knocked out"
    val monoPlaceTitles = listOf("👑 Master", "🃏 Card Shark", "🎯 Sharpshot", "🔥 Close Call", "🙂 Good Try", "🎲 Next Time", "🫠 Hoarder", "🐢 Slowpoke")

    fun monoPlace(place: Int) = when (place) {
        1 -> "1st"
        2 -> "2nd"
        3 -> "3rd"
        else -> "${place}th"
    }

    /** The match clock, "m:ss". */
    fun monoClock(minutes: Long, seconds: Long) = "$minutes:${seconds.toString().padStart(2, '0')}"

    /** The turn timer pill, "0:15". */
    fun monoTurnClock(seconds: Int) = "0:${seconds.coerceAtLeast(0).toString().padStart(2, '0')}"
}
