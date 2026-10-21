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

    /** The callout badge and the action button. */
    const val monoCallout = "MONO!"
    const val monoCallMono = "MONO!"
    const val monoCalled = "MONO!"

    const val monoDrawCard = "DRAW CARD"
    /** Declines the card just drawn and ends the turn. */
    const val monoPass = "PASS"
    const val monoDrawPile = "DRAW"
    const val monoDiscardPile = "DISCARD"
    fun monoStack(penalty: Int) = "STACK: +$penalty CARDS"
    fun monoTakePenalty(penalty: Int) = "DRAW $penalty"

    const val monoHand = "YOUR HAND"
    fun monoHandCount(count: Int) = "YOUR HAND · $count"
    fun monoCards(count: Int) = if (count == 1) "1 card" else "$count cards"

    const val monoYourTurn = "YOUR TURN"
    const val monoWaiting = "Waiting…"
    const val monoOpenFailed = "Could not open this match"

    const val monoChooseColour = "Choose a colour"
    const val monoChooseRouletteColour = "Name the colour your victim must draw to"

    const val monoColourRed = "Red"
    const val monoColourGreen = "Green"
    const val monoColourBlue = "Blue"
    const val monoColourGold = "Gold"

    const val monoAway = "Away"
    const val monoBot = "CPU"
    const val monoAutoplay = "Autoplay"
    const val monoMic = "Microphone"
    const val monoEliminated = "ELIMINATED"
    /** The seat is protected after calling MONO! at one card. */
    const val monoShield = "MONO!"

    /** Rule of 7: choose whose hand to trade. */
    const val monoSwapTitle = "Swap hands"
    const val monoSwapBody = "Choose the player you want to trade your whole hand with."
    /** Rule of 0: everyone passes a hand on. */
    const val monoPassTitle = "Hands pass on"
    const val monoPassBody = "Everyone passes their hand to the next player."

    const val monoYouWin = "You win!"
    fun monoWinner(name: String) = "$name wins!"
    const val monoYouKnockedOut = "You're out"
    fun monoKnockedOut(name: String) = "$name is out"
    const val monoLeaveWhenDone = "Close the table to go back to the dashboard."
    const val monoClose = "Close"
    const val monoSpectators = "spectators"

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
