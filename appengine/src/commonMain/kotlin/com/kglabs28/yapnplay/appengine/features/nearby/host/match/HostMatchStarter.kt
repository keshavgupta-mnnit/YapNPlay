package com.kglabs28.yapnplay.appengine.features.nearby.host.match

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
                userId = holder?.userId ?: (CPU_USER_PREFIX + cpuNumber),
                displayName = holder?.name ?: (CPU_NAME + cpuNumber),
                colour = session.colourOf(seat),
                cpu = holder == null,
                avatarId = holder?.avatarId ?: if (cpuNumber % 2 == 1) CPU_AVATAR_ODD else CPU_AVATAR_EVEN,
            )
        }
        val state = HostMatchState(newMatchId(), plan.gameId, roomId, plan.title, session, seats.toMutableList(), clock.nowMillis())
        plan.spectators.forEach { state.spectators[it.userId] = it.name to it.avatarId }
        return HostMatch(state, clock, settings, envelopes, scope, deliver, onFinished)
    }

    private fun newMatchId(): String = MATCH_ID_PREFIX + buildString { repeat(ID_LENGTH) { append(ID_ALPHABET[Random.nextInt(ID_ALPHABET.length)]) } }

    private companion object {
        const val CPU_USER_PREFIX = "cpu-"
        const val CPU_NAME = "CPU "
        const val CPU_AVATAR_ODD = "a09"
        const val CPU_AVATAR_EVEN = "a18"
        const val MATCH_ID_PREFIX = "nearby-match-"
        const val ID_LENGTH = 16
        const val ID_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"
    }
}
