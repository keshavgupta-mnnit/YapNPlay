package com.kglabs28.yapnplay.appengine.features.nearby.host.match

import com.kglabs28.yapnplay.appengine.protocol.EndVoteInfo
import com.kglabs28.yapnplay.appengine.protocol.ExtensionVoteInfo
import com.kglabs28.yapnplay.appengine.protocol.MatchSeatStatus
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.SeatInfo
import com.kglabs28.yapnplay.appengine.protocol.SpectatorInfo
import com.kglabs28.yapnplay.appengine.protocol.TurnInfo
import com.kglabs28.yapnplay.appengine.protocol.ViewerInfo
import com.kglabs28.yapnplay.appengine.protocol.ViewerRole
import kotlinx.serialization.json.JsonObject

/**
 * The snapshot one viewer sees, as the backend builds it: only their own
 * legal moves and the game's per-viewer public state (no hidden cards leak).
 */
class HostSnapshotBuilder(private val state: HostMatchState) {

    fun build(viewerUserId: String, now: Long, events: List<JsonObject>): MatchSnapshot {
        val session = state.session
        val viewerSeat = state.seatOf(viewerUserId)?.seat
        val running = !session.isFinished && !state.onHold
        return MatchSnapshot(
            matchId = state.matchId,
            version = state.version,
            serverTime = now,
            viewer = if (viewerSeat != null) ViewerInfo(ViewerRole.PLAYER, viewerSeat) else ViewerInfo(ViewerRole.SPECTATOR),
            seats = state.seats.map { it.toWire() },
            publicState = session.publicState(viewerSeat),
            legalMoves = if (running) session.legalMoves(viewerSeat) else emptyList(),
            turn = TurnInfo(session.currentSeat, session.phase, if (running && !session.waitsForTick) state.deadline else null),
            gameId = state.gameId,
            title = state.title,
            roomId = state.roomId,
            startedAt = state.startedAt,
            spectators = state.spectators.map { (userId, info) -> SpectatorInfo(userId, info.first, avatarId = info.second) },
            events = events,
            endVote = state.endVote?.let { EndVoteInfo(it.proposerSeat, it.endsAt, it.voters.sorted(), it.yes.sorted(), it.no.sorted()) },
            extensionVote = state.extensionVote?.let { vote ->
                val offer = session.extension
                ExtensionVoteInfo(vote.endsAt, vote.voters.sorted(), vote.yes.sorted(), vote.no.sorted(), offer.extraRounds, offer.extraMinutes)
            },
        )
    }

    private fun HostMatchSeat.toWire() = SeatInfo(
        seat = seat,
        colour = colour,
        controllerUserId = userId,
        status = MatchSeatStatus.ACTIVE,
        connected = connected,
        autoPlay = autoplay,
        consecutiveMisses = 0,
        cpu = cpu,
        displayName = displayName,
        micOn = micOn,
        presence = presence,
        avatarId = avatarId,
    )
}
