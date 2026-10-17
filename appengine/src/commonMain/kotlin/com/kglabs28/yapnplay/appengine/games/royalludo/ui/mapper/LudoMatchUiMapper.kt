package com.kglabs28.yapnplay.appengine.games.royalludo.ui.mapper

import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatch
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPhase
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.AvatarRing
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.BoardCorner
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoAvatarUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPieceUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoResultRowUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.PieceMotion
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.LudoBoardGeometry
import kotlin.time.Duration

/** Turns the match into what the table screen draws. Pure functions. */
object LudoMatchUiMapper {

    fun pieceId(seat: Int, piece: Int) = "$seat-$piece"

    /** Every piece of every seated player, at the shown progress. */
    fun pieces(
        match: LudoMatch,
        shown: Map<Int, List<Int>>,
        motions: Map<String, PieceMotion>,
        selectable: List<Int>,
    ): List<LudoPieceUi> = match.players.filter { !it.removed }.flatMap { player ->
        (shown[player.seat] ?: match.pieces[player.seat].orEmpty()).mapIndexed { piece, progress ->
            val id = pieceId(player.seat, piece)
            LudoPieceUi(
                id = id,
                seat = player.seat,
                piece = piece,
                colour = player.colour,
                progress = progress,
                motion = motions[id] ?: PieceMotion.SHIFT,
                selectable = player.isMe && piece in selectable,
            )
        }
    }

    /** What every player's card needs; [remainingMs] is null while a move animates. */
    data class CardInputs(
        val rotation: Int,
        val speaking: Set<String>,
        val mutedForMe: Set<String>,
        val remainingMs: Long?,
        val turnSeconds: Long,
    )

    /** Every player's card, the user's first, then the others clockwise from the user. */
    fun cards(match: LudoMatch, inputs: CardInputs): List<LudoPlayerCardUi> {
        val totalMs = inputs.turnSeconds * MILLIS
        val left = inputs.remainingMs?.coerceAtMost(totalMs) ?: totalMs
        val all = match.players.map { player ->
            val isTurn = player.seat == match.currentSeat && match.phase != LudoPhase.FINISHED && !player.removed
            val muted = player.userId in inputs.mutedForMe
            val place = match.finishedSeats.indexOf(player.seat)
            LudoPlayerCardUi(
                seat = player.seat,
                userId = player.userId,
                name = player.name,
                level = player.level.toString(),
                colour = player.colour,
                corner = BoardCorner.entries[LudoBoardGeometry.cornerOf(player.colour.ordinal, inputs.rotation)],
                isMe = player.isMe,
                isTurn = isTurn,
                talking = player.userId in inputs.speaking && player.micOn && !muted,
                timerFraction = if (isTurn) left.toFloat() / totalMs else 0f,
                left = player.removed,
                placeLabel = if (place >= 0) Strings.ludoPlace(place + 1) else null,
                mutedForMe = muted,
            )
        }
        val myCorner = all.firstOrNull { it.isMe }?.corner?.ordinal ?: 0
        return all.sortedBy { (it.corner.ordinal - myCorner + CORNERS) % CORNERS }
    }

    /** "1st", "2nd"… inside each finished player's base. */
    fun basePlaces(match: LudoMatch): Map<LudoColour, String> = match.finishedSeats.mapIndexedNotNull { index, seat ->
        match.players.firstOrNull { it.seat == seat }?.let { it.colour to Strings.ludoPlace(index + 1) }
    }.toMap()

    /** Match clock text, "m:ss". */
    fun elapsed(time: Duration): String {
        val total = time.inWholeSeconds.coerceAtLeast(0)
        return Strings.ludoClock(total / SECONDS_PER_MINUTE, total % SECONDS_PER_MINUTE)
    }

    /** Top bar avatars: the spectators (not the players), ringed by voice state. */
    fun audience(match: LudoMatch, speaking: Set<String>, mutedForMe: Set<String>): List<LudoAvatarUi> =
        match.spectators.map { LudoAvatarUi(it.userId, it.name, ring(it.userId in speaking && it.userId !in mutedForMe, it.micOn)) }

    fun resultRows(match: LudoMatch): List<LudoResultRowUi> = match.ranking.mapIndexedNotNull { index, seat ->
        match.players.firstOrNull { it.seat == seat }?.let { LudoResultRowUi(Strings.ludoPlace(index + 1), it.name, it.colour) }
    }

    private fun ring(talking: Boolean, micOn: Boolean) = when {
        !micOn -> AvatarRing.MUTED
        talking -> AvatarRing.TALKING
        else -> AvatarRing.IDLE
    }

    private const val MILLIS = 1_000L
    private const val CORNERS = 4
    private const val SECONDS_PER_MINUTE = 60L
}
