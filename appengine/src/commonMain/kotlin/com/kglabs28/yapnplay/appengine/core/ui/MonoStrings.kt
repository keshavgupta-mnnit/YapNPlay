package com.kglabs28.yapnplay.appengine.core.ui

/** MONO and MONO: Complete Chaos: the table, the cards and the results. */
object MonoStrings {
    const val monoTitle = "MONO"
    const val monoChaosTitle = "MONO: COMPLETE CHAOS"

    const val monoCallout = "MONO!"
    const val monoCallMono = "MONO!"
    const val monoCalled = "MONO!"

    const val monoDrawCard = "DRAW CARD"
    const val monoDiscardAll = "+ALL"
    const val monoCaughtButton = "CAUGHT"
    const val monoMonoButton = "MONO!"
    const val monoWordmark = "MONO"
    const val monoAll = "ALL"
    const val monoChaosPlate = "COMPLETE CHAOS"
    const val monoDrawPile = "DRAW"
    const val monoDiscardPile = "DISCARD"
    fun monoStack(penalty: Int) = "STACK: +$penalty CARDS"
    fun monoTakePenalty(penalty: Int) = "DRAW $penalty"

    const val monoHand = "YOUR HAND"
    fun monoHandCount(count: Int) = "YOUR HAND · $count"
    fun monoCards(count: Int) = if (count == 1) "1 card" else "$count cards"

    const val monoYourTurn = "YOUR TURN"
    const val monoWaiting = "Waiting…"

    const val monoChooseColour = "Choose a colour"
    const val monoChooseRouletteColour = "Name the colour your victim must draw to"

    const val monoColourRed = "Red"
    const val monoColourGreen = "Green"
    const val monoColourBlue = "Blue"
    const val monoColourGold = "Gold"

    const val monoAway = "Away"
    const val monoBot = "CPU"
    const val monoAutoplay = "Autoplay"
    const val monoEliminated = "ELIMINATED"
    const val monoShield = "MONO!"

    const val monoSwapTitle = "Swap hands"
    const val monoSwapBody = "Choose the player you want to trade your whole hand with."
    const val monoPassTitle = "Hands pass on"
    const val monoPassBody = "Everyone passes their hand to the next player."

    const val monoYouWin = "You win!"
    fun monoWinner(name: String) = "$name wins!"
    const val monoYouKnockedOut = "You're out"
    fun monoKnockedOut(name: String) = "$name is out"
    const val monoLeaveWhenDone = "Close the table to go back to the dashboard."
    const val monoClose = "Close"
    val monoPlaceTitles = listOf("Champion", "Runner-up", "Bronze", "Finisher", "Participant")
    const val monoFinished = "Finished"
    const val monoKnockedOutScore = "Knocked out"
    fun monoCardsLeft(cards: Int) = "$cards cards left"
    const val monoSpectators = "spectators"
    fun monoSpectatorCount(count: Int) = if (count == 1) "1 spectator" else "$count spectators"
    const val monoVoteTitle = "Vote to continue?"
    fun monoVoteBody(seconds: Long) = "The match continues if enough players agree. ${seconds}s left."
    const val monoVoteEnd = "End match"
    const val monoVoteKeep = "Keep playing"
    const val monoVoteWaiting = "Waiting for votes…"
    const val monoOpenFailed = "Could not open this match"
    fun monoYouFinishBanner(place: String) = "You finished! $place"
    fun monoFinishBanner(name: String, place: String) = "$name finished! $place"
    fun monoVoteCount(yes: Int, no: Int, voters: Int) = "$yes yes · $no no · $voters voters"
    fun monoTakeButton(penalty: Int) = "TAKE $penalty"
    const val monoDrawButton = "DRAW CARD"
    const val monoPass = "PASS"
    const val monoMic = "Microphone"

    fun monoPlace(place: Int) = when (place) {
        1 -> "1st"
        2 -> "2nd"
        3 -> "3rd"
        else -> "${place}th"
    }

    fun monoClock(minutes: Long, seconds: Long) = "$minutes:${seconds.toString().padStart(2, '0')}"
    fun monoTurnClock(seconds: Int) = "0:${seconds.coerceAtLeast(0).toString().padStart(2, '0')}"
}