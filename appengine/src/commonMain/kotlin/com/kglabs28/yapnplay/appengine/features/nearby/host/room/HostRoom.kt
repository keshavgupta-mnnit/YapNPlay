package com.kglabs28.yapnplay.appengine.features.nearby.host.room

import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyPlayer
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostClock
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostConstants
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostRefusal
import com.kglabs28.yapnplay.appengine.games.base.GameRegistry
import com.kglabs28.yapnplay.appengine.games.base.rules.GameDescriptor
import com.kglabs28.yapnplay.appengine.protocol.CountdownStop
import com.kglabs28.yapnplay.appengine.protocol.CountdownStopReason
import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
import com.kglabs28.yapnplay.appengine.protocol.Presence
import com.kglabs28.yapnplay.appengine.protocol.RoomInfo
import com.kglabs28.yapnplay.appengine.protocol.RoomMember
import com.kglabs28.yapnplay.appengine.protocol.RoomSnapshot

/**
 * The Quick Play party room a phone hosts for Nearby play: the backend's
 * temporary room, kept on the phone. One leader (the host to begin with)
 * picks the game and starts; anyone may get ready or watch. Pure state:
 * [HostParty] runs it one request at a time and sends the snapshots.
 */
class HostRoom(
    val roomId: String,
    val name: String,
    host: NearbyPlayer,
    private val registry: GameRegistry,
    private val clock: HostClock,
) {
    private val members = LinkedHashMap<String, HostRoomMember>()
    var leaderId: String = host.userId
        private set
    var selectedGame: String? = null
        private set
    var houseRules: Map<String, Boolean> = emptyMap()
        private set
    var botFill: Boolean = true
        private set
    var countdownDeadline: Long? = null
        private set
    var countdownStop: CountdownStop? = null
        private set
    var matchInProgress: String? = null
    var version = 0L
        private set

    init {
        add(host)
    }

    val isFull: Boolean get() = members.size >= HostConstants.MAX_PLAYERS

    fun has(userId: String): Boolean = userId in members

    fun onlineIds(): List<String> = members.values.filter { it.online }.map { it.userId }

    /** Someone accepted the host's invite: a member at once (they come online when their lobby opens). */
    fun add(player: NearbyPlayer) {
        if (player.userId in members) return
        members[player.userId] = HostRoomMember(player.userId, player.name, player.avatarId, clock.nowMillis())
    }

    /** Their lobby opened (or came back after a match). */
    fun enter(userId: String) {
        val member = member(userId)
        if (!member.online) members[userId] = member.copy(online = true, onlineSince = clock.nowMillis())
        // Whoever comes in takes over from a leader who isn't here.
        if (members[leaderId]?.online != true) leaderId = userId
    }

    /** Out of the lobby: gone from the party (a temporary room keeps nobody who left). */
    fun remove(userId: String) {
        val name = members[userId]?.displayName ?: return
        members.remove(userId)
        if (leaderId == userId) {
            members.values.filter { it.online }.minByOrNull { it.onlineSince ?: Long.MAX_VALUE }?.let { leaderId = it.userId }
        }
        revalidate(CountdownStopReason.LEFT, userId, name)
    }

    fun selectGame(userId: String, gameId: String?) {
        requireLeader(userId)
        if (gameId != null && registry.games.none { it.id.value == gameId }) throw HostRefusal(ErrorCode.GAME_UNAVAILABLE, NO_SUCH_GAME)
        stopCountdown(CountdownStopReason.GAME_CHANGED, userId)
        selectedGame = gameId
        houseRules = game()?.houseRuleOptions?.associate { it.key to it.defaultEnabled }.orEmpty()
        members.replaceAll { _, m -> m.copy(ready = false, readyAt = null, watching = false) }
    }

    fun setHouseRule(userId: String, key: String, enabled: Boolean) {
        requireLeader(userId)
        val game = game() ?: throw HostRefusal(ErrorCode.BAD_REQUEST, NO_GAME)
        if (game.houseRuleOptions.none { it.key == key }) throw HostRefusal(ErrorCode.BAD_REQUEST, NO_SUCH_RULE)
        houseRules = houseRules + (key to enabled)
    }

    fun setBotFill(userId: String, enabled: Boolean) {
        requireLeader(userId)
        botFill = enabled
        revalidate(CountdownStopReason.SETTINGS_CHANGED, userId)
    }

    fun setReady(userId: String, ready: Boolean) {
        member(userId)
        game() ?: throw HostRefusal(ErrorCode.BAD_REQUEST, NO_GAME)
        val now = clock.nowMillis()
        update(userId) { if (ready) copy(ready = true, readyAt = readyAt ?: now, watching = false) else copy(ready = false, readyAt = null) }
        revalidate(CountdownStopReason.NOT_READY, userId)
    }

    fun setWatching(userId: String, watching: Boolean) = update(userId) { copy(watching = watching) }

    fun setVoice(userId: String, micOn: Boolean, speakerOn: Boolean) =
        update(userId) { copy(micOn = micOn, speakerOn = speakerOn, talkingMuted = talkingMuted && !micOn) }

    fun setTalkingMuted(userId: String, talking: Boolean) = update(userId) { copy(talkingMuted = talking) }

    fun transferLeader(userId: String, targetId: String) {
        requireLeader(userId)
        if (members[targetId]?.online != true) throw HostRefusal(ErrorCode.BAD_REQUEST, TARGET_OFFLINE)
        leaderId = targetId
        revalidate(CountdownStopReason.SETTINGS_CHANGED, userId)
    }

    /** The leader starts the countdown; returns its deadline (the party schedules the end). */
    fun startCountdown(userId: String, seconds: Int): Long? {
        requireLeader(userId)
        val game = game() ?: throw HostRefusal(ErrorCode.BAD_REQUEST, NO_GAME)
        if (countdownDeadline != null) return null
        if (matchInProgress != null) throw HostRefusal(ErrorCode.BAD_REQUEST, MATCH_RUNNING)
        if (!canStart(game)) throw HostRefusal(ErrorCode.NOT_ENOUGH_READY, NOT_ENOUGH_READY)
        val deadline = clock.nowMillis() + seconds * HostConstants.MILLIS_PER_SECOND
        countdownDeadline = deadline
        countdownStop = null
        return deadline
    }

    /** Any ready player (the leader counts as ready) may cancel. */
    fun cancelCountdown(userId: String) {
        val member = member(userId)
        if (countdownDeadline == null) return
        if (!member.ready && leaderId != userId) throw HostRefusal(ErrorCode.BAD_REQUEST, NOT_READY_TO_CANCEL)
        stopCountdown(CountdownStopReason.CANCELLED, userId, member.displayName)
    }

    /** Zero reached for [deadline]: who plays (leader first, then by ready time), or null when it can't start. */
    fun countdownElapsed(deadline: Long): SeatPlan? {
        if (countdownDeadline != deadline) return null
        countdownDeadline = null
        val game = game()
        if (game == null || !canStart(game)) {
            countdownStop = CountdownStop(CountdownStopReason.START_FAILED, at = clock.nowMillis())
            return null
        }
        val players = readyHumans().take(game.maxPlayers)
        val bots = if (botFill && game.cpuFillAllowed && players.size < game.minPlayers) game.minPlayers - players.size else 0
        val spectators = members.values.filter { it.online && it !in players }
        return SeatPlan(game.id.value, game.displayName, players.map { it.player() }, bots, spectators.map { it.player() }, houseRules)
    }

    /** A match just started: everyone's ready/watching resets. */
    fun matchStarted(matchId: String) {
        matchInProgress = matchId
        members.replaceAll { _, m -> m.copy(ready = false, readyAt = null, watching = false) }
    }

    fun failedToStart() {
        countdownStop = CountdownStop(CountdownStopReason.START_FAILED, at = clock.nowMillis())
    }

    /** Something changed: the next snapshot gets a new version. */
    fun touch() {
        version++
    }

    fun snapshot(): RoomSnapshot = RoomSnapshot(
        version = version,
        serverTime = clock.nowMillis(),
        room = RoomInfo(
            id = roomId,
            code = roomId,
            name = name,
            leaderId = leaderId,
            adminId = leaderId,
            icon = ROOM_ICON,
            members = members.values.map { it.toWire() },
            selectedGame = selectedGame,
            botFill = botFill,
            houseRules = houseRules,
            entryCoins = 0,
            countdownDeadline = countdownDeadline,
            matchInProgress = matchInProgress,
            countdownStop = countdownStop,
            temporary = true,
        ),
    )

    private fun game(): GameDescriptor? = selectedGame?.let { id -> registry.games.firstOrNull { it.id.value == id } }

    private fun readyHumans(): List<HostRoomMember> =
        members.values.filter { it.online && (it.ready || it.userId == leaderId) }
            .sortedWith(compareBy<HostRoomMember>({ it.userId != leaderId }, { it.readyAt ?: Long.MAX_VALUE }))

    /** Offline a lone player may play the CPUs. */
    private fun canStart(game: GameDescriptor): Boolean {
        val ready = readyHumans().size
        return ready >= 1 && (ready >= game.minPlayers || (botFill && game.cpuFillAllowed))
    }

    private fun revalidate(reason: CountdownStopReason, byUserId: String, byName: String? = members[byUserId]?.displayName) {
        if (countdownDeadline == null) return
        val game = game()
        if (game == null || !canStart(game)) stopCountdown(reason, byUserId, byName)
    }

    private fun stopCountdown(reason: CountdownStopReason, byUserId: String?, byName: String? = byUserId?.let { members[it]?.displayName }) {
        if (countdownDeadline == null) return
        countdownDeadline = null
        countdownStop = CountdownStop(reason, byUserId, byName, clock.nowMillis())
    }

    private fun requireLeader(userId: String) {
        member(userId)
        if (leaderId != userId) throw HostRefusal(ErrorCode.NOT_LEADER, NOT_LEADER)
    }

    private fun member(userId: String): HostRoomMember = members[userId] ?: throw HostRefusal(ErrorCode.NOT_MEMBER, NOT_MEMBER)

    private fun update(userId: String, change: HostRoomMember.() -> HostRoomMember) {
        members[userId] = member(userId).change()
    }

    private fun HostRoomMember.player() = NearbyPlayer(userId, displayName, avatarId)

    private fun HostRoomMember.toWire() = RoomMember(
        userId = userId,
        displayName = displayName,
        presence = if (online) Presence.ONLINE else Presence.OFFLINE,
        ready = ready,
        watching = watching,
        micOn = micOn,
        talkingMuted = talkingMuted && !micOn,
        speakerOn = speakerOn,
        onlineSince = onlineSince,
        avatarId = avatarId,
    )

    private companion object {
        const val ROOM_ICON = "group"
        const val NO_SUCH_GAME = "That game isn't here."
        const val NO_GAME = "Pick a game first."
        const val NO_SUCH_RULE = "This game has no such rule."
        const val TARGET_OFFLINE = "Only someone here can become leader."
        const val MATCH_RUNNING = "A match is already running."
        const val NOT_ENOUGH_READY = "Not enough players are ready."
        const val NOT_READY_TO_CANCEL = "Only a ready player can cancel."
        const val NOT_LEADER = "Only the leader can do that."
        const val NOT_MEMBER = "You are not in this party."
    }
}

/** Who plays when a party's countdown ends: seated people (leader first), CPU count, and those who watch. */
data class SeatPlan(
    val gameId: String,
    val title: String,
    val players: List<NearbyPlayer>,
    val cpuCount: Int,
    val spectators: List<NearbyPlayer>,
    val houseRules: Map<String, Boolean>,
)
