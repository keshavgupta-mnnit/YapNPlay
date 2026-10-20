package com.kglabs28.yapnplay.appengine.games.royalludo.ui.mapper

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.components.profile.AvatarPresets
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoMatch
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPhase
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoPresence
import com.kglabs28.yapnplay.appengine.ui.components.table.AvatarRing
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.BoardCorner
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoMatchModal
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPieceUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPlayerCardUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoResultRowUi
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.PieceMotion
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.LudoBoardGeometry
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatHandoverRules
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOfferStage
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverUiMapper
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatPickOptionUi
import kotlinx.datetime.Instant
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
            // A seat the CPU plays for someone who left looks just like a CPU seat.
            val cpuPlays = player.isCpu || player.presence == LudoPresence.BOT
            LudoPlayerCardUi(
                seat = player.seat,
                userId = player.userId,
                name = if (cpuPlays && !player.isCpu) Strings.seatBot else player.name,
                avatarId = if (cpuPlays && !player.isCpu) AvatarPresets.CPU else player.avatarId,
                // CPUs have no level.
                level = if (cpuPlays) "" else player.level.toString(),
                colour = player.colour,
                corner = BoardCorner.entries[LudoBoardGeometry.cornerOf(player.colour.ordinal, inputs.rotation)],
                isMe = player.isMe,
                isTurn = isTurn,
                talking = player.userId in inputs.speaking && player.micOn && !muted,
                timerFraction = if (isTurn) left.toFloat() / totalMs else 0f,
                left = player.removed,
                placeLabel = if (place >= 0) Strings.ludoPlace(place + 1) else null,
                mutedForMe = muted,
                canSpeak = !cpuPlays,
                statusLabel = if (match.seatOffer?.seat == player.seat) seatStatus(match.seatOffer.stage) else when (player.presence) {
                    LudoPresence.AWAY -> Strings.ludoAway
                    LudoPresence.BOT -> null
                    LudoPresence.PLAYING -> null
                },
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

    private fun seatStatus(stage: SeatOfferStage) = if (stage is SeatOfferStage.ConfirmingAbsence) Strings.seatIdle else Strings.seatOffered

    fun leaveBody(match: LudoMatch): String = when {
        match.me == null -> Strings.seatSpectatorLeaveBody
        match.isRoomMatch -> Strings.seatLeaveBody
        else -> Strings.ludoLeaveBody
    }

    /** Spectator layout: the cards for the houses on the board's left (or right) side, the upper house first. */
    fun side(cards: List<LudoPlayerCardUi>, left: Boolean): List<LudoPlayerCardUi> {
        val (upper, lower) = if (left) BoardCorner.TOP_LEFT to BoardCorner.BOTTOM_LEFT else BoardCorner.TOP_RIGHT to BoardCorner.BOTTOM_RIGHT
        return cards.filter { it.corner == upper } + cards.filter { it.corner == lower }
    }

    /** The handover strip everyone sees, or null. */
    fun seatBanner(match: LudoMatch, now: Instant): String? = match.seatOffer?.let { SeatHandoverUiMapper.banner(it, now) }

    /** "Give your seat to" choices: the bot and the spectators in a room match; none in Quick Play or when only watching. */
    fun leaveOptions(match: LudoMatch): List<SeatPickOptionUi> =
        if (match.isRoomMatch && match.me != null) SeatHandoverUiMapper.options(match.spectators.map { it.name }, withBot = true) else emptyList()

    /**
     * The dialog this viewer owes a handover: accept/decline when offered the
     * seat by name, the pick when they are the deciding leader, or (as a
     * spectator) the open seat they have not turned down; "still here?" after
     * idle turns.
     */
    fun seatModal(match: LudoMatch, now: Instant, declinedSeat: Int?, pick: Int?): LudoMatchModal? {
        val offer = match.seatOffer ?: return null
        val seconds = SeatHandoverUiMapper.secondsLeft(offer, now)
        val countdown = SeatHandoverUiMapper.countdown(offer, now)
        val spectatorNames = match.spectators.map { it.name }
        return when (val stage = offer.stage) {
            is SeatOfferStage.ConfirmingAbsence -> if (stage.userId != match.viewerUserId) {
                null
            } else {
                LudoMatchModal.AbsenceCheck(
                    title = Strings.seatInactiveTitle,
                    body = Strings.seatAbsenceBody(SeatHandoverRules.IDLE_TURNS_BEFORE_CHECK, match.isRoomMatch),
                    countdown = countdown,
                    // The bot first and picked by default; spectators only in a room match.
                    options = SeatHandoverUiMapper.options(if (match.isRoomMatch) spectatorNames else emptyList(), withBot = true),
                    selectedIndex = pick ?: 0,
                )
            }
            is SeatOfferStage.ToSpectator -> if (stage.userId != match.viewerUserId) {
                null
            } else {
                LudoMatchModal.SeatOfferPrompt(Strings.seatPromptTitle(offer.leaverName), Strings.seatPromptBody(offer.leaverName), Strings.seatTake(seconds))
            }
            is SeatOfferStage.LeaderChoosing -> if (stage.leaderUserId != match.viewerUserId) {
                null
            } else {
                LudoMatchModal.LeaderSeatChoice(
                    title = Strings.seatOpenTitle,
                    body = Strings.seatLeaderBody(offer.leaverName),
                    countdown = countdown,
                    // The bot is a button here, not a tile.
                    options = SeatHandoverUiMapper.options(spectatorNames, withBot = false),
                    selectedIndex = pick,
                )
            }
            SeatOfferStage.OpenToAll -> if (match.me != null || offer.seat == declinedSeat) {
                null
            } else {
                LudoMatchModal.SeatInvite(offer.seat, Strings.seatInviteTitle(offer.leaverName), Strings.seatInviteBody, Strings.seatTake(seconds))
            }
        }
    }

    /** Top bar avatars: the spectators (not the players), ringed by voice state. */
    fun audience(match: LudoMatch, speaking: Set<String>, mutedForMe: Set<String>): List<TableAvatarUi> =
        match.spectators.map { TableAvatarUi(it.userId, it.name, ring(it.userId in speaking && it.userId !in mutedForMe, it.micOn)) }

    /** The end-of-match leaderboard: finishing order, a title, points and EXP for each place. */
    fun leaderboard(match: LudoMatch, duration: String): LeaderboardUi {
        val count = match.ranking.size
        val entries = match.ranking.mapIndexedNotNull { index, seat ->
            match.players.firstOrNull { it.seat == seat }?.let { p ->
                LeaderboardEntryUi(
                    place = index + 1,
                    placeLabel = Strings.ludoPlace(index + 1),
                    name = p.name,
                    avatarId = p.avatarId,
                    title = Strings.ludoPlaceTitles[index.coerceAtMost(Strings.ludoPlaceTitles.lastIndex)],
                    score = Strings.leaderboardPts(AppConstants.PLACE_EXP[index.coerceAtMost(AppConstants.PLACE_EXP.lastIndex)]),
                    exp = Strings.leaderboardExp(AppConstants.PLACE_EXP[index.coerceAtMost(AppConstants.PLACE_EXP.lastIndex)]),
                    micOn = p.micOn,
                    tint = Color.Unspecified,
                    isMe = p.isMe,
                )
            }
        }
        return LeaderboardUi(Strings.ludoTitle, duration, entries, entries.firstOrNull { it.isMe }?.placeLabel.orEmpty(), myExp = entries.firstOrNull { it.isMe }?.let { e -> "+" + AppConstants.PLACE_EXP[(e.place - 1).coerceAtMost(AppConstants.PLACE_EXP.lastIndex)] }.orEmpty())
    }

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
    private const val POINTS_PER_PLACE = 100
    private const val SECONDS_PER_MINUTE = 60L
}
