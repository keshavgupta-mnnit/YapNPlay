package com.kglabs28.yapnplay.appengine.ui.screens.mono

import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatch
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoPlayer
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatch
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosPlayer
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import com.kglabs28.yapnplay.appengine.ui.components.table.AvatarRing
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoCardColours
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi

/**
 * Turns a Standard MONO match into the cards the table draws. Shared by
 * both MONO games: they differ in the model, not in how a player reads.
 */
object MonoUiMapper {

    /** The colour a seat wears: the four card colours, in seat order. */
    fun seatTint(seat: Int): Color = SEAT_TINTS[(seat - 1).mod(SEAT_TINTS.size)]

    /** [player] as the card the table draws. */
    fun card(
        player: MonoPlayer,
        currentSeat: Int,
        timerFraction: Float,
        places: Map<Int, String>,
    ) = MonoPlayerCardUi(
        seat = player.seat,
        name = player.name,
        avatarId = player.avatarId.orEmpty(),
        level = if (player.isCpu) "" else player.level.toString(),
        tint = seatTint(player.seat),
        isMe = player.isMe,
        isTurn = player.seat == currentSeat,
        talking = false,
        cards = player.cardCount,
        timerFraction = timerFraction,
        placeLabel = places[player.seat],
        statusLabel = statusLabel(player.presence, player.autoplay, player.isCpu),
        canSpeak = !player.isCpu,
    )

    /** [player] as the card the table draws (Complete Chaos). */
    fun chaosCard(
        player: MonoChaosPlayer,
        currentSeat: Int,
        timerFraction: Float,
        places: Map<Int, String>,
        eliminated: Set<Int>,
    ) = MonoPlayerCardUi(
        seat = player.seat,
        name = player.name,
        avatarId = player.avatarId.orEmpty(),
        level = if (player.isCpu) "" else player.level.toString(),
        tint = seatTint(player.seat),
        isMe = player.isMe,
        isTurn = player.seat == currentSeat,
        talking = false,
        cards = player.cardCount,
        timerFraction = timerFraction,
        placeLabel = places[player.seat],
        // Knocked out at 25 cards: their name stays, their count does not.
        statusLabel = if (player.seat in eliminated) {
            Strings.monoEliminated
        } else {
            statusLabel(player.presence, player.autoplay, player.isCpu)
        },
        canSpeak = !player.isCpu,
    )

    /** Finishing places from the match's ranking; empty while it plays. */
    fun places(match: MonoMatch): Map<Int, String> =
        match.ranking.mapIndexed { index, entry -> entry.seat to placeLabel(index + 1) }.toMap()

    /** Finishing places from the match's ranking (Complete Chaos). */
    fun chaosPlaces(match: MonoChaosMatch): Map<Int, String> =
        match.ranking.mapIndexed { index, entry -> entry.seat to placeLabel(index + 1) }.toMap()

    /** The seat that emptied their hand first, if anyone has. */
    fun winner(match: MonoMatch): Int? = match.finishedSeats.minOrNull()?.takeIf { match.phase == FINISHED }

    /** The seat that emptied their hand first (Complete Chaos). */
    fun chaosWinner(match: MonoChaosMatch): Int? =
        match.finishedSeats.minOrNull()?.takeIf { match.phase == FINISHED }

    /** "1st" … "8th" for a finishing place. */
    fun placeLabel(place: Int): String = when (place) {
        1 -> Strings.monoPlace1
        2 -> Strings.monoPlace2
        3 -> Strings.monoPlace3
        else -> "${place}${Strings.monoPlaceSuffix}"
    }

    /** Spectators and absent players, for the top bar strip. */
    fun audience(match: MonoMatch) = match.spectators.map {
        TableAvatarUi(it.userId, it.name, if (it.micOn) AvatarRing.TALKING else AvatarRing.IDLE)
    }

    /** Spectators and absent players (Complete Chaos). */
    fun chaosAudience(match: MonoChaosMatch) = match.spectators.map {
        TableAvatarUi(it.userId, it.name, if (it.micOn) AvatarRing.TALKING else AvatarRing.IDLE)
    }

    /** "Away" / "Bot" while the server plays for someone. */
    private fun statusLabel(presence: String, autoplay: Boolean, isCpu: Boolean): String? = when {
        presence == SeatPresence.AWAY.name -> Strings.monoAway
        presence == SeatPresence.BOT.name || isCpu -> Strings.monoBot
        autoplay -> Strings.monoAutoplay
        else -> null
    }

    private val SEAT_TINTS = listOf(
        MonoCardColours.RED,
        MonoCardColours.GREEN,
        MonoCardColours.BLUE,
        MonoCardColours.GOLD,
    )

    private const val FINISHED = "FINISHED"

    /** Standard MONO's colour set, so callers need no extra import. */
    val COLOURS: List<MonoConstants.Color> = MonoConstants.Color.entries

    /** Complete Chaos's colour set. */
    val CHAOS_COLOURS: List<MonoChaosConstants.Color> = MonoChaosConstants.Color.entries
}