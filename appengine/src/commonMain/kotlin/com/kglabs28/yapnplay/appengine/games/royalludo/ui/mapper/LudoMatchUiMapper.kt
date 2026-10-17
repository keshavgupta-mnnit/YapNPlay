package com.kglabs28.yapnplay.appengine.games.royalludo.ui.mapper

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatch
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPhase
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.AvatarRing
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.BoardCorner
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoAvatarUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPieceUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoResultRowUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.PieceMotion
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.PingQuality
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.LudoBoardGeometry

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
                motion = motions[id] ?: PieceMotion.HOP,
                selectable = player.isMe && piece in selectable,
            )
        }
    }

    /**
     * Player cards in their colour's corner. [remainingMs] is the turn
     * time left (null while a move is still animating: the bar stays full).
     */
    fun cards(
        match: LudoMatch,
        rotation: Int,
        speaking: Set<String>,
        remainingMs: Long?,
        turnSeconds: Long,
    ): Map<BoardCorner, LudoPlayerCardUi> = match.players.map { player ->
        val isTurn = player.seat == match.currentSeat && match.phase != LudoPhase.FINISHED && !player.removed
        val totalMs = turnSeconds * MILLIS
        val left = remainingMs?.coerceAtMost(totalMs) ?: totalMs
        LudoPlayerCardUi(
            seat = player.seat,
            name = player.name,
            level = player.level.toString(),
            colour = player.colour,
            corner = BoardCorner.entries[LudoBoardGeometry.cornerOf(player.colour.ordinal, rotation)],
            isMe = player.isMe,
            isTurn = isTurn,
            talking = player.userId in speaking && player.micOn,
            timerFraction = if (isTurn) left.toFloat() / totalMs else 0f,
            left = player.removed,
        )
    }.associateBy { it.corner }

    /** Top bar avatars: the spectators (not the players), ringed by voice state. */
    fun audience(match: LudoMatch, speaking: Set<String>): List<LudoAvatarUi> =
        match.spectators.map { LudoAvatarUi(it.userId, it.name, ring(it.userId in speaking, it.micOn)) }

    fun resultRows(match: LudoMatch): List<LudoResultRowUi> = match.ranking.mapIndexedNotNull { index, seat ->
        match.players.firstOrNull { it.seat == seat }?.let { LudoResultRowUi(Strings.ludoPlace(index + 1), it.name, it.colour) }
    }

    /** Ping colour band: good under AppConstants.PING_GOOD_MS, fair under AppConstants.PING_FAIR_MS. */
    fun pingQuality(latencyMs: Int?): PingQuality = when {
        latencyMs == null || latencyMs < AppConstants.PING_GOOD_MS -> PingQuality.GOOD
        latencyMs < AppConstants.PING_FAIR_MS -> PingQuality.FAIR
        else -> PingQuality.POOR
    }

    private fun ring(talking: Boolean, micOn: Boolean) = when {
        !micOn -> AvatarRing.MUTED
        talking -> AvatarRing.TALKING
        else -> AvatarRing.IDLE
    }

    private const val MILLIS = 1_000L
}
