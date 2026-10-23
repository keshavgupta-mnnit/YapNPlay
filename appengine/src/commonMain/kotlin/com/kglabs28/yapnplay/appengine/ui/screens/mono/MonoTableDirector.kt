package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.appmanager.helpers.GamesAppManagerHelper
import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.core.audio.GameSound
import com.kglabs28.yapnplay.appengine.core.audio.TurnAlarm
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardLook
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoFlight
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoSpot

/**
 * What just happened at a MONO table, as the user hears and sees it:
 * a sound per event and every card that moves flying from where it was
 * to where it went. Shared by both MONO games — each maps its own server
 * events onto [Cue]s, the director does the rest. Nothing sounds while
 * the table is in the background.
 */
class MonoTableDirector(
    private val audio: GameAudio,
    gamesConfig: GamesAppManagerHelper,
    private val chaos: Boolean,
) {
    /** Something that happened at the table, in the game's own words already resolved. */
    sealed interface Cue {
        /** [seat] played [look]; the discard pile's top was [cover] before it. */
        data class Played(val seat: Int, val look: MonoCardLook, val cover: MonoCardLook?) : Cue
        /** [seat] drew [count] cards; [looks] are their faces when the viewer may see them. */
        data class Drew(val seat: Int, val count: Int, val looks: List<MonoCardLook> = emptyList()) : Cue
        data object Shuffled : Cue
        /** Discard All took [count] more cards from [seat]'s hand onto the pile; [looks] are their faces when the viewer may see them. */
        data class Discarded(val seat: Int, val count: Int, val looks: List<MonoCardLook> = emptyList()) : Cue
        /** [count] cards went from [fromSeat]'s hand to [toSeat]'s (a swap or everyone passing); [looks] when the viewer receives them. */
        data class Moved(val fromSeat: Int, val toSeat: Int, val count: Int, val looks: List<MonoCardLook> = emptyList()) : Cue
        data object Called : Cue
        data object Caught : Cue
        data object Won : Cue
    }

    var foreground = true
        private set
    private var nextId = 0L
    private var wasMyTurn = false
    private val turnAlarm = TurnAlarm(hurrySeconds = { gamesConfig.turnHurrySeconds() }) { sound(it) }

    /** The table came to the front or went away: music follows it. */
    fun setForeground(visible: Boolean) {
        foreground = visible
        if (visible) audio.startMusic() else audio.stopMusic()
    }

    fun stop() = audio.stopMusic()

    /** The user's turn: a chime when it starts, then the clock ticking down. */
    fun turn(myTurn: Boolean, remainingMs: Long) {
        if (myTurn && !wasMyTurn) sound(GameSound.YOUR_TURN)
        wasMyTurn = myTurn
        turnAlarm.update(myTurn, remainingMs)
    }

    /** The user pressed MONO / CAUGHT: it sounds at once, before the server answers. */
    fun pressed(sound: GameSound) = sound(sound)

    /**
     * Plays the sounds for [cues] and returns the cards to fly, one after
     * another. [mySeat] is the viewer's seat (their cards fly to and from
     * the hand along the bottom).
     */
    fun direct(cues: List<Cue>, mySeat: Int?): List<MonoFlight> {
        if (!foreground) return emptyList()
        val flights = mutableListOf<MonoFlight>()
        var delay = 0
        cues.forEach { cue ->
            when (cue) {
                is Cue.Played -> {
                    sound(GameSound.CARD_PLAY)
                    flights += MonoFlight(
                        id = nextId++,
                        // The user's own card leaves from where they tapped it.
                        from = if (cue.seat == mySeat) MonoSpot.PlayedCard else spotOf(cue.seat, mySeat),
                        to = MonoSpot.Discard,
                        look = cue.look,
                        delayMs = delay,
                        cover = cue.cover,
                        chaos = chaos,
                    )
                    delay += PLAY_GAP_MS
                }
                is Cue.Drew -> {
                    sound(GameSound.CARD_DRAW)
                    // The user's own cards all fly, each to its sorted place; others' big draws only a few.
                    repeat(cue.count.coerceAtMost(if (cue.seat == mySeat) MAX_OWN_DRAWS else MAX_FLYING_DRAWS)) { index ->
                        flights += MonoFlight(
                            id = nextId++,
                            from = MonoSpot.Deck,
                            to = spotOf(cue.seat, mySeat),
                            look = cue.looks.getOrNull(index),
                            delayMs = delay,
                            chaos = chaos,
                        )
                        delay += DRAW_GAP_MS
                    }
                }
                Cue.Shuffled -> {
                    sound(GameSound.CARD_SHUFFLE)
                    // The discard pile (all but its top) streams back onto the deck.
                    repeat(SHUFFLE_CARDS) {
                        flights += MonoFlight(id = nextId++, from = MonoSpot.Discard, to = MonoSpot.Deck, look = null, delayMs = delay, chaos = chaos)
                        delay += SHUFFLE_GAP_MS
                    }
                }
                is Cue.Discarded -> {
                    sound(GameSound.CARD_PLAY)
                    repeat(cue.count.coerceAtMost(MAX_FLYING_DRAWS)) { index ->
                        val look = cue.looks.getOrNull(index)
                        flights += MonoFlight(id = nextId++, from = spotOf(cue.seat, mySeat), to = MonoSpot.Discard, look = look, delayMs = delay, cover = look, chaos = chaos)
                        delay += DRAW_GAP_MS
                    }
                }
                is Cue.Moved -> {
                    sound(GameSound.CARD_DRAW)
                    repeat(cue.count.coerceAtMost(if (cue.toSeat == mySeat) MAX_OWN_DRAWS else MAX_FLYING_DRAWS)) { index ->
                        flights += MonoFlight(
                            id = nextId++,
                            from = spotOf(cue.fromSeat, mySeat),
                            to = spotOf(cue.toSeat, mySeat),
                            look = cue.looks.getOrNull(index),
                            delayMs = delay,
                            chaos = chaos,
                        )
                        delay += DRAW_GAP_MS
                    }
                }
                Cue.Called -> sound(GameSound.MONO_CALL)
                Cue.Caught -> sound(GameSound.CAUGHT)
                Cue.Won -> sound(GameSound.WIN)
            }
        }
        return flights
    }

    private fun spotOf(seat: Int, mySeat: Int?): MonoSpot = if (seat == mySeat) MonoSpot.Hand else MonoSpot.Seat(seat)

    private fun sound(sound: GameSound) {
        if (foreground) audio.play(sound)
    }

    private companion object {
        const val PLAY_GAP_MS = 320
        const val DRAW_GAP_MS = 140
        const val SHUFFLE_GAP_MS = 60
        /** The reshuffle shows this many cards streaming back; the deck's height shows the rest. */
        const val SHUFFLE_CARDS = 8
        /** A big penalty still flies only this many cards; the fan shows the rest. */
        const val MAX_FLYING_DRAWS = 10
        /** The user's own new cards all fly (each lands in its sorted place), up to the knockout size. */
        const val MAX_OWN_DRAWS = 30
    }
}
