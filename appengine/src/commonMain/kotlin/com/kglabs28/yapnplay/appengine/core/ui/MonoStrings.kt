package com.kglabs28.yapnplay.appengine.core.ui

import com.kglabs28.yapnplay.appengine.ui.components.moderation.GameRuleUi
/**
 * MONO and MONO: Complete Chaos: the table, the cards and the results.
 * Both games share these words; only the title differs.
 */
object MonoStrings {
    /** The rules shown from the settings menu. */
    /** How both MONO games rank players when they end early. */
    const val monoEndRanking = "Players still in are ranked by fewest cards."

    val monoRules: List<GameRuleUi> = listOf(
        GameRuleUi("Goal", "Be the first to play all your cards. The others play on for the next places."),
        GameRuleUi("Your turn", "Play one card that matches the top card by colour, number or symbol. A Wild can go on anything."),
        GameRuleUi("Nothing to play", "Draw one card. If it fits you may play it, or pass. Your turn time starts again."),
        GameRuleUi("Skip", "The next player misses their turn."),
        GameRuleUi("Reverse", "Play changes direction. With two players it works like a Skip."),
        GameRuleUi("+2", "The next player draws 2 cards and misses their turn."),
        GameRuleUi("Wild", "Choose the colour that is in play next."),
        GameRuleUi("Wild +4", "Choose the colour; the next player draws 4 cards and misses their turn."),
        GameRuleUi("MONO!", "When you are down to one card, press MONO! before anyone catches you."),
        GameRuleUi("CAUGHT", "Press CAUGHT when a player on one card forgot MONO!: they draw 5 cards. Pressing it with nobody to catch costs you 2 cards."),
        GameRuleUi("Out of time", "If your time runs out, the table plays a card or draws for you."),
        GameRuleUi("Places", "Every player who empties their hand takes the next place. After the second finisher, the table votes whether to play on."),
        GameRuleUi(TableStrings.endEarlyRuleTitle, TableStrings.endEarlyRule(monoEndRanking)),
    )

    /** The rules shown from the settings menu. */
    val chaosRules: List<GameRuleUi> = listOf(
        GameRuleUi("Goal", "Empty your hand first, or be the last player still in after everyone else is knocked out."),
        GameRuleUi("Your turn", "Play one card that matches the top card by colour, number or symbol. A Wild can go on anything."),
        GameRuleUi("Draw until you can play", "With nothing to play, you draw one card at a time until one fits. That card you may play, or pass."),
        GameRuleUi("Knockout at 25", "Reach 25 cards at any moment and you are knocked out of the match."),
        GameRuleUi("Stacking", "On a draw card you may stack a draw card worth the same or more (+2, +4, Wild Reverse +4, Wild +6, Wild +10). The first player who cannot stack takes the whole total."),
        GameRuleUi("Wild Reverse +4, +6, +10", "Choose the colour; the penalty passes on. Wild Reverse +4 also reverses play; the next player in the new direction takes it."),
        GameRuleUi("Wild Roulette", "The next player picks a colour, then flips cards one by one until that colour comes up. Every flipped card goes into their hand and their turn ends."),
        GameRuleUi("Rule of 7", "Playing a 7: pick an opponent and swap your whole hands."),
        GameRuleUi("Rule of 0", "Playing a 0: every player passes their whole hand to the next player, the way play is going."),
        GameRuleUi("Discard All", "Discards every card of its colour from your hand along with it."),
        GameRuleUi("Skip Everyone", "Everyone else is skipped: you play again at once."),
        GameRuleUi("MONO!", "When you are down to one card, press MONO! before anyone catches you."),
        GameRuleUi("CAUGHT", "Press CAUGHT when a player on one card forgot MONO!: they draw 7 cards. Pressing it with nobody to catch costs you 2 cards."),
        GameRuleUi(TableStrings.endEarlyRuleTitle, TableStrings.endEarlyRule(monoEndRanking)),
    )


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
    const val monoChooseRouletteColour = "Wild Roulette hit you!"
    const val monoChooseRouletteColourHint = "Pick a colour: you'll flip cards until one of that colour comes up"


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
