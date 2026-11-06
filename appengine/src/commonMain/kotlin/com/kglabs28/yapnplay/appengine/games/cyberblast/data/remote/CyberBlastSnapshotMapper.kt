package com.kglabs28.yapnplay.appengine.games.cyberblast.data.remote

import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.features.seathandover.data.toDomain
import com.kglabs28.yapnplay.appengine.games.base.match.MatchEndVote
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastPhase
import com.kglabs28.yapnplay.appengine.games.cyberblast.FlameShape
import com.kglabs28.yapnplay.appengine.games.cyberblast.data.remote.wire.CyberBlastPublicState
import com.kglabs28.yapnplay.appengine.games.cyberblast.data.remote.wire.CyberFrame
import com.kglabs28.yapnplay.appengine.games.cyberblast.data.remote.wire.CyberRelay
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBody
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberBombData
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberFlameData
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberFrameData
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberHitData
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberItemData
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberMatch
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberSeat
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberSpectator
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberUpdate
import com.kglabs28.yapnplay.appengine.protocol.MatchSeatStatus
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Duration.Companion.milliseconds

/** `MatchSnapshot` (Cyber Blast's public state) → [CyberUpdate]; a relayed `CyberFrame` → [CyberFrameData]. */
class CyberBlastSnapshotMapper(private val json: Json) {

    fun map(snapshot: MatchSnapshot, myUserId: String, now: Instant): CyberUpdate {
        val state = json.decodeFromJsonElement(CyberBlastPublicState.serializer(), snapshot.publicState)
        val toLocal = { serverMillis: Long -> now + (serverMillis - snapshot.serverTime).milliseconds }
        val out = state.frame.players.associate { it.seat to it.out }
        val match = CyberMatch(
            matchId = snapshot.matchId,
            title = snapshot.title,
            roomId = snapshot.roomId,
            seats = snapshot.seats.map { seat ->
                CyberSeat(
                    seat = seat.seat,
                    userId = seat.controllerUserId,
                    name = seat.displayName,
                    level = seat.level,
                    avatarId = seat.avatarId,
                    isMe = seat.seat == snapshot.viewer.seat,
                    micOn = seat.micOn,
                    isCpu = seat.cpu,
                    away = seat.presence != SeatPresence.PLAYING,
                    botHeld = seat.presence == SeatPresence.BOT,
                    removed = seat.status == MatchSeatStatus.REMOVED && out[seat.seat] != false,
                )
            },
            spectators = snapshot.spectators.map { CyberSpectator(it.userId, it.displayName, it.micOn, it.avatarId) },
            cols = state.cols,
            rows = state.rows,
            lives = state.lives,
            frameMs = state.frameMs,
            startedAt = toLocal(snapshot.startedAt),
            viewerUserId = myUserId,
            seatOffer = snapshot.seatOffer?.toDomain(toLocal),
            canOfferSeats = snapshot.canOfferSeats,
            endVote = snapshot.endVote?.let { MatchEndVote(it.proposerSeat, toLocal(it.endsAt), it.voters, it.yes, it.no) },
        )
        return CyberUpdate(match, frame(state.frame))
    }

    /** A relayed frame; null for anything else on the relay (an emoji gift shares it). */
    fun relayFrame(payload: JsonObject): CyberFrameData? = if (payload[TYPE]?.jsonPrimitive?.contentOrNull != FRAME) null else runCatching {
        when (val relay = json.decodeFromJsonElement(CyberRelay.serializer(), payload)) {
            is CyberFrame -> frame(relay)
        }
    }.onFailure { CrashReporter.record(it, "CyberBlastSnapshotMapper.frame") }.getOrNull()

    private fun frame(f: CyberFrame) = CyberFrameData(
        number = f.frame,
        time = f.time,
        phase = BlastPhase.entries.firstOrNull { it.name == f.phase } ?: BlastPhase.PLAYING,
        countdownMs = f.countdownMs,
        closingInMs = f.closingInMs,
        bodies = f.players.map {
            CyberBody(it.seat, it.x, it.y, BlastDir.of(it.facing) ?: BlastDir.DOWN, it.moving, it.lives, it.score, it.radius, it.boostMs, it.shieldMs, it.out)
        },
        bombs = f.bombs.map { CyberBombData(it.id, it.tile, it.owner, it.radius, it.fuseMs) },
        flames = f.flames.map { CyberFlameData(it.tile, it.owner, FlameShape.entries.firstOrNull { s -> s.name == it.shape } ?: FlameShape.CENTRE, it.msLeft) },
        items = f.items.map { CyberItemData(it.tile, it.msLeft) },
        crates = f.crates?.toSet(),
        walls = f.walls?.toSet(),
        hits = f.hits.map { CyberHitData(it.victim, it.by, it.livesLeft, it.victimPoints, it.byPoints, it.self, it.crushed) },
        broken = f.broken,
        booms = f.booms,
    )

    private companion object {
        const val TYPE = "type"
        const val FRAME = "CyberFrame"
    }
}
