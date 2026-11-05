package com.kglabs28.yapnplay.appengine.features.nearby.host.match

import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostClock
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostEnvelopes
import com.kglabs28.yapnplay.appengine.features.nearby.host.games.GameSessionFactory
import com.kglabs28.yapnplay.appengine.features.nearby.host.room.SeatPlan
import com.kglabs28.yapnplay.appengine.features.nearby.host.settings.Settings
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import kotlinx.coroutines.CoroutineScope
import kotlin.random.Random

/**
 * Opens matches on the hosting phone, seating people the way the backend
 * does: players in a random order, CPUs filling up to the plan's count,
 * everyone else at the party watching.
 */
class HostMatchStarter(
    private val factories: List<GameSessionFactory>,
    private val clock: HostClock,
    private val settings: Settings,
    private val envelopes: HostEnvelopes,
) {
    fun start(
        roomId: String,
        plan: SeatPlan,
        scope: CoroutineScope,
        deliver: (String, Envelope) -> Unit,
        onFinished: (String) -> Unit,
        onChanged: () -> Unit = {},
    ): HostMatch? {
        val factory = factories.firstOrNull { it.game.id.value == plan.gameId } ?: return null
        val seatCount = plan.players.size + plan.cpuCount
        val session = factory.start(seatCount, plan.houseRules)
        val holders = (plan.players + List(plan.cpuCount) { null }).shuffled()
        val seats = holders.mapIndexed { index, holder ->
            val seat = index + 1
            val cpuNumber = holders.take(index + 1).count { it == null }
            HostMatchSeat(
                seat = seat,
                userId = holder?.userId ?: HostCpu.userId(cpuNumber),
                displayName = holder?.name ?: HostCpu.name(cpuNumber),
                colour = session.colourOf(seat),
                cpu = holder == null,
                avatarId = holder?.avatarId ?: HostCpu.avatar(cpuNumber),
            )
        }
        val state = HostMatchState(newMatchId(), plan.gameId, roomId, plan.title, session, seats.toMutableList(), clock.nowMillis())
        plan.spectators.forEach { state.spectators[it.userId] = it.name to it.avatarId }
        return HostMatch(state, clock, settings, envelopes, scope, deliver, onFinished, onChanged)
    }

    /**
     * A match taken over from a host who left ([gone]): the game as [save] holds it, their seat
     * given up (the bot plays it), everyone else's waiting for their phone to come back.
     */
    fun restore(
        save: HostMatchSave,
        gone: String,
        scope: CoroutineScope,
        deliver: (String, Envelope) -> Unit,
        onFinished: (String) -> Unit,
        onChanged: () -> Unit,
    ): HostMatch? {
        val factory = factories.firstOrNull { it.game.id.value == save.gameId } ?: return null
        val session = runCatching { factory.restore(save.session) }.getOrNull() ?: return null
        val now = clock.nowMillis()
        val state = HostMatchState(save.matchId, save.gameId, save.roomId, save.title, session, save.seats.toMutableList(), save.startedAt)
        save.spectators.forEach { (userId, who) -> if (userId != gone) state.spectators[userId] = who.getOrElse(0) { "" } to who.getOrElse(1) { "" } }
        state.version = save.version
        state.deadline = save.deadline
        state.pauseMs = save.pauseMs
        state.botWakeAt = save.botWakeAt
        state.turnOwner = save.turnOwner
        state.extensionVote = save.extensionVote?.let { v -> HostExtensionVote(v.voters, v.endsAt).also { it.yes += v.yes; it.no += v.no } }
        state.endVote = save.endVote?.let { v -> HostEndVote(v.proposerSeat, v.voters, v.startedAt, v.endsAt).also { it.yes += v.yes; it.no += v.no } }
        state.lastEndProposal += save.lastEndProposal
        state.waitingFor += save.waitingFor - gone
        state.startHoldUntil = save.startHoldUntil
        state.seats.filter { !it.cpu && !it.left }.forEach { seat ->
            if (seat.userId == gone) {
                val out = session.stillIn.let { it.isNotEmpty() && seat.seat !in it }
                state.update(seat.seat) { copy(left = true, leftAt = now, presence = SeatPresence.BOT, outBeforeLeaving = out) }
            } else {
                // Their phone is finding the new host: the bot covers them only if they don't come back in time.
                state.update(seat.seat) { copy(connected = false, disconnectedAt = now) }
            }
        }
        return HostMatch(state, clock, settings, envelopes, scope, deliver, onFinished, onChanged)
    }

    private fun newMatchId(): String = MATCH_ID_PREFIX + buildString { repeat(ID_LENGTH) { append(ID_ALPHABET[Random.nextInt(ID_ALPHABET.length)]) } }

    private companion object {
        const val MATCH_ID_PREFIX = "nearby-match-"
        const val ID_LENGTH = 16
        const val ID_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"
    }
}
