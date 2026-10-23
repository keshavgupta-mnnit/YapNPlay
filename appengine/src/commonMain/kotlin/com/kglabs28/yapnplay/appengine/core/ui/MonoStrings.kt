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
    const val monoCaughtButton = "CAUGHT"
    const val monoMonoButton = "MONO"
    // What just happened at the table, one line at a time (both games).
    fun monoColourName(colour: String) = when (colour) {
        "RED" -> "Red"
        "GREEN" -> "Green"
        "BLUE" -> "Blue"
        else -> "Gold"
    }
    fun monoNoteColour(name: String, colour: String) = "$name changed the colour to $colour"
    fun monoNoteYouColour(colour: String) = "You changed the colour to $colour"
    fun monoNoteRoulette(name: String, colour: String) = "Roulette: $name flips cards until $colour comes"
    fun monoNoteYouRoulette(colour: String) = "Roulette: you flip cards until $colour comes"
    fun monoNoteStacked(name: String, total: Int) = "$name stacked it up: +$total now"
    fun monoNoteYouStacked(total: Int) = "You stacked it up: +$total now"
    fun monoNoteTookStack(name: String, count: Int) = "$name took the stack: +$count cards"
    fun monoNoteYouTookStack(count: Int) = "You took the stack: +$count cards"
    fun monoNoteStackOnYou(total: Int) = "+$total stacked on you: nothing to add, taking the cards"
    fun monoNoteDumped(name: String, count: Int) = "$name dumped $count more cards of that colour"
    fun monoNoteYouDumped(count: Int) = "You dumped $count more cards of that colour"
    fun monoNoteCaught(catcher: String, caught: String, count: Int) = "$catcher caught $caught: +$count cards"
    fun monoNoteYouWereCaught(catcher: String, count: Int) = "$catcher caught you without MONO: +$count cards"
    fun monoNoteYouCaught(caught: String, count: Int) = "You caught $caught: +$count cards"
    fun monoNoteWrongCatch(name: String, count: Int) = "$name: wrong catch, +$count cards"
    fun monoNoteYouWrongCatch(count: Int) = "Wrong catch! +$count cards"
    fun monoNoteMissed(name: String, count: Int) = "$name forgot to call MONO: +$count cards"
    fun monoNoteYouMissed(count: Int) = "You forgot to call MONO: +$count cards"
    fun monoNoteSwapped(a: String, b: String) = "$a swapped hands with $b"
    const val monoNotePassed = "Everyone passed their hand on"
    fun monoNoteKnockedOut(name: String) = "$name is knocked out"
    const val monoNoteYouKnockedOut = "You're knocked out"
    const val monoCantCallYet = "Call MONO when you're down to your last card"
    const val monoDrawButton = "Draw Card"
    fun monoTakeButton(penalty: Int) = "Take +$penalty"
    fun monoSpectatorCount(count: Int) = if (count == 1) "1 Spectator" else "$count Spectators"

    /** Declines the card just drawn and ends the turn. */
    const val monoPass = "PASS"
    fun monoStack(penalty: Int) = "STACK: +$penalty CARDS"


    const val monoYourTurn = "YOUR TURN"
    const val monoOpenFailed = "Could not open this match"

    const val monoChooseColour = "Choose a colour"
    const val monoChooseRouletteColour = "Wild Roulette hit you! Pick a colour: you'll flip cards until one of that colour comes up"


    const val monoAway = "Away"
    const val monoAutoplay = "Autoplay"
    const val monoMicShort = "Mic"
    const val monoAutoShort = "Auto"
    const val monoMic = "Microphone"
    const val monoEliminated = "ELIMINATED"

    /** Rule of 7: choose whose hand to trade. */
    const val monoSwapTitle = "Swap hands"
    const val monoSwapBody = "Choose the player you want to trade your whole hand with."
    fun monoSwapCards(count: Int) = if (count == 1) "1 card" else "$count cards"


    /** The banner when a player empties their hand. */
    fun monoFinishBanner(name: String, place: String) = "🏆 $name finishes $place!"
    fun monoYouFinishBanner(place: String) = "🏆 You finish $place!"
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

    fun monoPlace(place: Int) = TableStrings.placeLabel(place)

    /** The match clock, "m:ss". */
    fun monoClock(minutes: Long, seconds: Long) = "$minutes:${seconds.toString().padStart(2, '0')}"

    /** The turn timer pill, "0:15". */
    fun monoTurnClock(seconds: Int) = "0:${seconds.coerceAtLeast(0).toString().padStart(2, '0')}"
}
