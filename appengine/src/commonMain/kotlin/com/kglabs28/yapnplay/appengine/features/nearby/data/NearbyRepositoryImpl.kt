package com.kglabs28.yapnplay.appengine.features.nearby.data

import com.kglabs28.yapnplay.appengine.features.nearby.domain.HostFitness
import com.kglabs28.yapnplay.appengine.features.nearby.host.PartyClock
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.delay
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyHostResult
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyRadio
import com.kglabs28.yapnplay.appengine.core.network.OfflineMode
import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyInvite
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyPeer
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyPlayer
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyProblem
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyRole
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyState
import com.kglabs28.yapnplay.appengine.features.nearby.domain.PeerInvite
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostEnvelopes
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostParty
import com.kglabs28.yapnplay.appengine.features.nearby.host.match.HostMatchChannel
import com.kglabs28.yapnplay.appengine.features.nearby.host.match.HostMatchStarter
import com.kglabs28.yapnplay.appengine.features.nearby.host.room.HostRoom
import com.kglabs28.yapnplay.appengine.features.nearby.protocol.CloseReason
import com.kglabs28.yapnplay.appengine.features.nearby.protocol.NearbyFrame
import com.kglabs28.yapnplay.appengine.features.nearby.repository.NearbyRepository
import com.kglabs28.yapnplay.appengine.features.nearby.transport.NearbyTransport
import com.kglabs28.yapnplay.appengine.features.nearby.transport.TransportEvent
import com.kglabs28.yapnplay.appengine.games.base.GameRegistry
import com.kglabs28.yapnplay.appengine.protocol.transport.CONTRACT_VERSION
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Nearby play on this phone.
 * - Findable: advertises who it is, so a host around can invite it.
 * - Hosting: a [HostParty] runs the party room and its games here; this
 *   phone's own lobby and tables talk to it directly, guests over the radio.
 * - Guest: lobby and table messages go to the host's phone over the radio.
 * While in a party [OfflineMode] is on, so every screen uses [gateway].
 */
class NearbyRepositoryImpl(
    private val transport: NearbyTransport,
    private val gateway: OfflineRealtimeGateway,
    private val mode: OfflineMode,
    private val identity: NearbyIdentity,
    private val starter: HostMatchStarter,
    private val matchChannel: HostMatchChannel,
    private val envelopes: HostEnvelopes,
    private val registry: GameRegistry,
    private val clock: PartyClock,
    private val frames: NearbyFrames,
    /** How well this phone could host (the party hands over to the best). */
    private val fitness: HostFitness,
    private val scope: CoroutineScope,
) : NearbyRepository {
    private val _state = MutableStateFlow(NearbyState())
    override val state: StateFlow<NearbyState> = _state.asStateFlow()
    override val supported: Boolean get() = transport.supported

    private var party: HostParty? = null
    private var me: NearbyPlayer? = null
    /** Host: each guest's link, by account id. */
    private val guestLinks = mutableMapOf<String, String>()
    /** Guest: the host's link. */
    private var hostLink: String? = null
    /** Host: shares the phones around with every guest whenever the list changes. */
    private var peersJob: Job? = null
    private var findable = false
    /** The app wants this phone findable (in front, permission given); it becomes so whenever it can. */
    private var wantFindable = false
    private var findableRetry: Job? = null
    /** Guest: this phone asked to leave, so the host letting go of it is no surprise. */
    private var leavingSelf = false
    /** Guest waiting for the party's new host after a handover: the party's id. */
    private var rejoining: String? = null
    /** New host after a handover: the party's people still to come back (let in without asking). */
    private val expected = mutableSetOf<String>()
    /** Gives up waiting on a handover after a while. */
    private var handoverJob: Job? = null
    /** Host: something changed, the guests' copies are due. */
    private val changes = MutableSharedFlow<Unit>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private var shadowJob: Job? = null
    /** Guest: the host's latest copy (who takes over, and the match if this phone is next). */
    private var lastShadow: NearbyFrame.Shadow? = null
    /** Guest: the host's clock minus this phone's, when [lastShadow] came. */
    private var shadowOffset = 0L
    /** Guest: tells the host how well this phone could host, every so often. */
    private var fitnessJob: Job? = null
    /** Host after a takeover: who comes before whom, should another phone host this party too. */
    private var successionOrder: List<String> = emptyList()
    private val leaveLock = Mutex()

    init {
        scope.launch { transport.events.collect { onEvent(it) } }
    }

    /**
     * Findable only while every switch Nearby needs is already on: Nearby
     * Connections would otherwise switch Bluetooth on by itself, without the
     * user saying so.
     */
    override suspend fun becomeFindable() {
        wantFindable = true
        tryFindable()
    }

    override fun stopBeingFindable() {
        wantFindable = false
        pauseFindable()
    }

    /**
     * Findable now if it is wanted and can be: free (not in a party) and every switch on. Tried
     * again when a switch comes on, when a party ends, and a little later should the radio refuse.
     */
    private suspend fun tryFindable() {
        findableRetry?.cancel()
        if (!wantFindable || !transport.supported || findable || _state.value.role != NearbyRole.IDLE) return
        if (transport.radios().values.any { !it }) return
        findable = transport.advertise(NearbyEndpointName.encode(identity.me()))
        if (!findable) {
            findableRetry = scope.launch {
                delay(FINDABLE_RETRY_MS)
                tryFindable()
            }
        }
    }

    /** Not findable for now (hosting, joining, a switch off); still wanted for later. */
    private fun pauseFindable() {
        findableRetry?.cancel()
        if (!findable) return
        findable = false
        transport.stopAdvertising()
    }

    override fun radios(): Map<NearbyRadio, Boolean> = transport.radios()

    override fun openRadioSettings(radio: NearbyRadio) = transport.openRadioSettings(radio)

    override suspend fun host(): NearbyHostResult {
        leave()
        if (transport.radios().values.any { !it }) return NearbyHostResult.Failed(null)
        pauseFindable()
        val host = identity.me()
        val room = HostRoom(ROOM_ID_PREFIX + newId(), NearbyStrings.partyName(host.name), host, registry, clock)
        if (!startHosting(room, host)) {
            tryFindable()
            return NearbyHostResult.Failed(transport.lastFailure)
        }
        return NearbyHostResult.Hosted(room.roomId)
    }

    /** Runs [room]'s party on this phone and looks for phones around; false when the radios can't. */
    private suspend fun startHosting(room: HostRoom, host: NearbyPlayer): Boolean {
        me = host
        val hosted = HostParty(
            room = room,
            starter = starter,
            matchChannel = matchChannel,
            envelopes = envelopes,
            scope = scope,
            deliver = ::deliver,
            onGuestLeft = ::letGo,
            onHostLeft = { scope.launch { leave() } },
            onChanged = { changes.tryEmit(Unit) },
        )
        hosted.register(host)
        party = hosted
        // Nothing opens until the radios really look for phones: a failed start leaves no lobby behind.
        if (!transport.discover()) {
            party = null
            me = null
            transport.stopAll()
            return false
        }
        gateway.sender = { request -> gateway.receive(hosted.handle(host.userId, request)) }
        mode.set(true)
        gateway.linkUp()
        _state.value = NearbyState(role = NearbyRole.HOSTING, roomId = room.roomId)
        peersJob?.cancel()
        peersJob = scope.launch {
            _state.map { it.peers }.distinctUntilChanged().collect { peers -> guestLinks.values.forEach { send(it, NearbyFrame.Peers(peers)) } }
        }
        // Every guest keeps a copy: who takes over, and (the next host only) the match. At most every so often.
        shadowJob?.cancel()
        shadowJob = scope.launch {
            changes.conflate().collect {
                sendShadow(hosted, host)
                if (passToBetterPhone(hosted, host)) return@collect
                delay(SHADOW_GAP_MS)
            }
        }
        hosted.fitness(host.userId, fitness.score())
        return true
    }

    /**
     * Host, in the lobby: a guest's phone is clearly better at hosting (battery, charger, memory),
     * so the party moves there and this phone carries on as a guest. Never mid-game. True when it moved.
     */
    private suspend fun passToBetterPhone(hosted: HostParty, host: NearbyPlayer): Boolean {
        if (hosted.matchRunning() || leaveLock.isLocked) return false
        hosted.fitness(host.userId, fitness.score())
        val frame = hosted.handover(host.userId, guestLinks.keys.toSet(), staying = true) ?: return false
        if (hosted.fitnessOf(frame.newHostId) < hosted.fitnessOf(host.userId) + BETTER_HOST_MARGIN) return false
        stepDown(frame)
        return true
    }

    /** Host: sends every guest who takes over if this phone goes, and the next host the match as it is. */
    private suspend fun sendShadow(hosted: HostParty, host: NearbyPlayer) {
        val plan = hosted.handover(host.userId, guestLinks.keys.toSet()) ?: return
        val match = hosted.matchSave()
        val now = clock.nowMillis()
        guestLinks.toMap().forEach { (userId, link) -> send(link, NearbyFrame.Shadow(plan, match.takeIf { userId == plan.newHostId }, now)) }
    }

    override suspend fun invite(endpointId: String) {
        // A guest asks the host, who sees the phones around and sends the invite.
        if (party == null) {
            hostLink?.takeIf { _state.value.role == NearbyRole.GUEST }?.let { send(it, NearbyFrame.AskInvite(endpointId)) }
            return
        }
        setPeer(endpointId, PeerInvite.INVITING)
        if (!transport.connect(endpointId, NearbyEndpointName.encode(identity.me()))) setPeer(endpointId, PeerInvite.NONE)
    }

    override suspend fun acceptInvite() {
        val invite = _state.value.incoming ?: return
        pauseFindable()
        hostLink = invite.endpointId
        _state.update { it.copy(role = NearbyRole.JOINING, incoming = null, roomId = invite.roomId) }
        send(invite.endpointId, NearbyFrame.Accept(identity.me(), CONTRACT_VERSION, fitness.score()))
    }

    override suspend fun declineInvite() {
        val invite = _state.value.incoming ?: return
        _state.update { it.copy(incoming = null) }
        send(invite.endpointId, NearbyFrame.Decline)
    }

    override fun isNearbyRoom(roomId: String): Boolean = _state.value.roomId == roomId

    override fun clearProblem() {
        _state.update { it.copy(problem = null) }
    }

    /**
     * Out of the party. The host hands it over to the one in the lobby longest (it ends
     * only when nobody else is there); a guest just goes.
     */
    override suspend fun leave() = leaveLock.withLock {
        val hosted = party
        if (hosted != null && handOver(hosted)) return@withLock
        shutDown(endForAll = true)
    }

    /** Host: tells everyone who takes over, then goes; false when there is nobody to take over. */
    private suspend fun handOver(hosted: HostParty): Boolean {
        val host = me ?: return false
        val frame = hosted.handover(host.userId, guestLinks.keys.toSet()) ?: return false
        // The next host gets the match as it is right now, then everyone the handover.
        sendShadow(hosted, host)
        guestLinks.values.forEach { send(it, frame) }
        // The handover goes out before the links close.
        delay(HANDOVER_SEND_MS)
        shutDown(endForAll = false)
        return true
    }

    /** Everything stops on this phone; [endForAll]: a host tells its guests the party is over. */
    private fun shutDown(endForAll: Boolean) {
        val hosted = party
        if (hosted != null) {
            if (endForAll) guestLinks.values.forEach { send(it, NearbyFrame.Closed(CloseReason.ENDED)) }
            hosted.close()
        }
        val wasInParty = _state.value.role != NearbyRole.IDLE
        peersJob?.cancel()
        peersJob = null
        handoverJob?.cancel()
        handoverJob = null
        shadowJob?.cancel()
        shadowJob = null
        fitnessJob?.cancel()
        fitnessJob = null
        lastShadow = null
        successionOrder = emptyList()
        clock.offsetMs = 0L
        party = null
        me = null
        guestLinks.clear()
        hostLink = null
        leavingSelf = false
        rejoining = null
        expected.clear()
        if (wasInParty) {
            transport.stopAll()
            findable = false
        }
        gateway.linkDown()
        gateway.sender = null
        mode.set(false)
        _state.update { NearbyState(problem = it.problem, incoming = it.incoming) }
        // Free again: findable once more, if wanted.
        if (wasInParty) scope.launch { tryFindable() }
    }

    override fun leaveInBackground() {
        scope.launch { leave() }
    }

    // ---------------- radio events ----------------

    private suspend fun onEvent(event: TransportEvent) {
        when (event) {
            is TransportEvent.PeerFound -> NearbyEndpointName.decode(event.name)?.let { player ->
                if (party == null) return@let
                // Another phone hosts this same party (both took over): only one may, the other steps down.
                NearbyEndpointName.hostedRoom(event.name)?.let { room ->
                    if (room == _state.value.roomId) yieldIfLater(player.userId)
                    return@let
                }
                _state.update { s -> s.copy(peers = s.peers.filterNot { it.endpointId == event.endpointId || it.player.userId == player.userId } + NearbyPeer(event.endpointId, player)) }
                // Someone of the party coming back after a handover: invited at once (their phone says yes by itself).
                if (expected.remove(player.userId)) invite(event.endpointId)
            }
            is TransportEvent.PeerLost -> _state.update { s ->
                // A phone already invited keeps its row (it stops advertising when it joins).
                s.copy(peers = s.peers.filterNot { it.endpointId == event.endpointId && it.invite == PeerInvite.NONE })
            }
            is TransportEvent.Connected -> onConnected(event.endpointId)
            is TransportEvent.ConnectFailed -> if (party != null) setPeer(event.endpointId, PeerInvite.NONE)
            is TransportEvent.Disconnected -> onDisconnected(event.endpointId)
            is TransportEvent.Received -> frames.decode(event.bytes)?.let { onFrame(event.endpointId, it) }
            // The user switched something off: stop being findable now, or Nearby would switch it back on.
            // A switch turned on: findable now, if wanted.
            TransportEvent.RadiosChanged -> if (transport.radios().values.any { !it }) pauseFindable() else tryFindable()
        }
    }

    /** Host: the link to a phone it invites is up, so the invite goes. */
    private fun onConnected(endpointId: String) {
        val hosted = party ?: return
        val host = me ?: return
        if (_state.value.peers.none { it.endpointId == endpointId && it.invite == PeerInvite.INVITING }) return
        send(endpointId, NearbyFrame.Invite(hosted.room.roomId, hosted.room.name, host, CONTRACT_VERSION))
        setPeer(endpointId, PeerInvite.INVITED)
    }

    private suspend fun onDisconnected(endpointId: String) {
        val hosted = party
        when {
            hosted != null -> {
                val userId = guestLinks.entries.firstOrNull { it.value == endpointId }?.key
                if (userId != null) {
                    guestLinks.remove(userId)
                    hosted.dropped(userId)
                }
                _state.update { s -> s.copy(peers = s.peers.filterNot { it.endpointId == endpointId }) }
            }
            // Asked to leave: the host letting go is expected, no "party ended".
            endpointId == hostLink -> when {
                leavingSelf -> leaveLock.withLock { shutDown(endForAll = false) }
                // The host's phone went without a word (closed, out of range): the party goes on, as its last copy said.
                lastShadow != null -> lastShadow?.let { takeOver(it.party) }
                else -> stopWith(NearbyProblem.HOST_LEFT)
            }
            _state.value.incoming?.endpointId == endpointId -> _state.update { it.copy(incoming = null) }
        }
    }

    private suspend fun onFrame(endpointId: String, frame: NearbyFrame) {
        val hosted = party
        when (frame) {
            // Someone around invites this phone (only while free).
            is NearbyFrame.Invite -> when {
                frame.contract != CONTRACT_VERSION -> if (_state.value.role == NearbyRole.IDLE || rejoining != null) {
                    _state.update { it.copy(problem = NearbyProblem.VERSION_MISMATCH) }
                    transport.disconnect(endpointId)
                }
                // The party's new host calling after a handover: back in without asking.
                rejoining == frame.roomId && _state.value.role == NearbyRole.JOINING -> {
                    transport.stopAdvertising()
                    hostLink = endpointId
                    send(endpointId, NearbyFrame.Accept(identity.me(), CONTRACT_VERSION, fitness.score()))
                }
                _state.value.role == NearbyRole.IDLE ->
                    _state.update { it.copy(incoming = NearbyInvite(endpointId, frame.roomId, frame.roomName, frame.host)) }
            }
            is NearbyFrame.Handover -> if (endpointId == hostLink) takeOver(frame)
            is NearbyFrame.Shadow -> if (endpointId == hostLink) {
                lastShadow = frame
                shadowOffset = frame.hostNow - clock.phoneNow()
            }
            is NearbyFrame.Accept -> if (hosted != null) admit(hosted, endpointId, frame)
            NearbyFrame.Decline -> if (hosted != null) {
                setPeer(endpointId, PeerInvite.DECLINED)
                transport.disconnect(endpointId)
            }
            is NearbyFrame.Welcome -> if (endpointId == hostLink) joined()
            is NearbyFrame.Message -> if (hosted != null) {
                val userId = guestLinks.entries.firstOrNull { it.value == endpointId }?.key ?: return
                send(endpointId, NearbyFrame.Message(hosted.handle(userId, frame.envelope)))
            } else if (endpointId == hostLink) {
                gateway.receive(frame.envelope)
            }
            is NearbyFrame.Peers -> if (endpointId == hostLink) _state.update { it.copy(peers = frame.peers) }
            is NearbyFrame.AskInvite -> if (hosted != null && endpointId in guestLinks.values) invite(frame.endpointId)
            is NearbyFrame.Fitness -> if (hosted != null) guestLinks.entries.firstOrNull { it.value == endpointId }?.key?.let { hosted.fitness(it, frame.score) }
            is NearbyFrame.Closed -> if (endpointId == hostLink) {
                stopWith(
                    when (frame.reason) {
                        CloseReason.ENDED -> NearbyProblem.HOST_LEFT
                        CloseReason.FULL -> NearbyProblem.JOIN_FAILED
                        CloseReason.VERSION_MISMATCH -> NearbyProblem.VERSION_MISMATCH
                    },
                )
            }
        }
    }

    /** Host: the invited phone said yes. */
    private suspend fun admit(hosted: HostParty, endpointId: String, accept: NearbyFrame.Accept) {
        val refusal = when {
            accept.contract != CONTRACT_VERSION -> CloseReason.VERSION_MISMATCH
            hosted.isFull() -> CloseReason.FULL
            else -> null
        }
        if (refusal != null) {
            send(endpointId, NearbyFrame.Closed(refusal))
            transport.disconnect(endpointId)
            setPeer(endpointId, PeerInvite.NONE)
            return
        }
        guestLinks[accept.player.userId] = endpointId
        hosted.register(accept.player)
        hosted.admit(accept.player)
        hosted.fitness(accept.player.userId, accept.fitness)
        setPeer(endpointId, PeerInvite.JOINED)
        send(endpointId, NearbyFrame.Welcome(hosted.room.roomId))
        send(endpointId, NearbyFrame.Peers(_state.value.peers))
    }

    /** Guest: the host let this phone in; its lobby opens next (or opens again, after a handover). */
    private fun joined() {
        val link = hostLink ?: return
        rejoining = null
        handoverJob?.cancel()
        handoverJob = null
        leavingSelf = false
        gateway.sender = { request ->
            if (request.type == MessageType.LEAVE_ROOM || request.type == MessageType.EXIT_LOBBY) leavingSelf = true
            send(link, NearbyFrame.Message(request))
        }
        mode.set(true)
        gateway.linkUp()
        _state.update { it.copy(role = NearbyRole.GUEST) }
        fitnessJob?.cancel()
        fitnessJob = scope.launch {
            while (true) {
                delay(FITNESS_EVERY_MS)
                send(link, NearbyFrame.Fitness(fitness.score()))
            }
        }
    }

    /**
     * Guest: the host left and [frame] says who hosts now. The lobby waits (its requests hold)
     * while this phone either hosts the party itself or waits to be let back in by the new host.
     */
    private suspend fun takeOver(frame: NearbyFrame.Handover) {
        hostLink = null
        gateway.linkDown()
        val mine = identity.me()
        val copy = lastShadow
        lastShadow = null
        if (frame.newHostId == mine.userId) {
            // On the old host's timeline, so the copied match's deadlines stay right.
            clock.offsetMs = shadowOffset
            val room = HostRoom(frame.roomId, frame.roomName, mine, registry, clock)
            room.restore(frame.selectedGame, frame.houseRules, frame.botFill)
            if (!startHosting(room, mine)) return stopWith(NearbyProblem.HOST_LEFT)
            frame.members.forEach { party?.register(it) }
            expected += frame.members.map { it.userId } - mine.userId
            successionOrder = frame.order
            // The game goes on here, the old host's seat given up.
            copy?.match?.let { party?.restoreMatch(it, frame.fromHostId) }
            // Says it hosts this party, so a second phone that also took over finds it and steps down.
            transport.advertise(NearbyEndpointName.encodeHost(mine, frame.roomId))
            // Whoever hasn't come back by then has gone.
            handoverJob = scope.launch {
                delay(HANDOVER_WAIT_MS)
                expected.clear()
            }
            return
        }
        rejoining = frame.roomId
        _state.update { it.copy(role = NearbyRole.JOINING, roomId = frame.roomId, peers = emptyList()) }
        transport.advertise(NearbyEndpointName.encode(mine))
        handoverJob = scope.launch {
            delay(HANDOVER_WAIT_MS)
            if (_state.value.role != NearbyRole.GUEST) stopWith(NearbyProblem.HOST_LEFT)
        }
    }

    /** Host: a message for one person (this phone's own screens, or a guest's phone). */
    private fun deliver(userId: String, envelope: Envelope) {
        if (userId == me?.userId) {
            scope.launch { gateway.receive(envelope) }
        } else {
            guestLinks[userId]?.let { send(it, NearbyFrame.Message(envelope)) }
        }
    }

    /**
     * Host after a takeover: [other] hosts this party too. The one earlier in the succession order
     * keeps it; if that is [other], this phone hands its guests over to them and rejoins as a guest.
     */
    private suspend fun yieldIfLater(other: String) {
        val mine = me ?: return
        val order = successionOrder
        val myRank = order.indexOf(mine.userId).takeIf { it >= 0 } ?: Int.MAX_VALUE
        val theirRank = order.indexOf(other).takeIf { it >= 0 } ?: Int.MAX_VALUE
        if (myRank < theirRank || (myRank == theirRank && mine.userId < other)) return
        val hosted = party ?: return
        stepDown(NearbyFrame.Handover(hosted.room.roomId, hosted.room.name, other, order, hosted.room.players(), hosted.room.selectedGame, hosted.room.houseRules, hosted.room.botFill, fromHostId = ""))
    }

    /** Host: hands the party to [frame]'s new host and carries on in it as a guest (the new host lets this phone back in). */
    private suspend fun stepDown(frame: NearbyFrame.Handover) {
        val mine = me ?: return
        val hosted = party ?: return
        sendShadow(hosted, mine)
        guestLinks.values.forEach { send(it, frame) }
        delay(HANDOVER_SEND_MS)
        leaveLock.withLock { shutDown(endForAll = false) }
        rejoining = frame.roomId
        mode.set(true)
        _state.update { it.copy(role = NearbyRole.JOINING, roomId = frame.roomId, peers = emptyList()) }
        transport.advertise(NearbyEndpointName.encode(mine))
        handoverJob = scope.launch {
            delay(HANDOVER_WAIT_MS)
            if (_state.value.role != NearbyRole.GUEST) stopWith(NearbyProblem.HOST_LEFT)
        }
    }

    /** Host: a guest left the lobby; their link goes (a moment later, so the reply to their leaving gets there first). */
    private fun letGo(userId: String) {
        val link = guestLinks.remove(userId) ?: return
        _state.update { s -> s.copy(peers = s.peers.filterNot { it.endpointId == link }) }
        scope.launch {
            delay(LET_GO_DELAY_MS)
            transport.disconnect(link)
        }
    }

    private fun setPeer(endpointId: String, invite: PeerInvite) {
        _state.update { s -> s.copy(peers = s.peers.map { if (it.endpointId == endpointId) it.copy(invite = invite) else it }) }
    }

    private suspend fun stopWith(problem: NearbyProblem) {
        leave()
        _state.update { it.copy(problem = problem) }
    }

    private fun send(endpointId: String, frame: NearbyFrame) {
        transport.send(endpointId, frames.encode(frame))
    }

    private fun newId(): String = buildString { repeat(ID_LENGTH) { append(ID_ALPHABET[Random.nextInt(ID_ALPHABET.length)]) } }

    private companion object {
        const val ROOM_ID_PREFIX = "nearby-room-"
        const val ID_LENGTH = 12
        const val ID_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"
        /** Time for a last message to go out before the link closes. */
        const val LET_GO_DELAY_MS = 1_500L
        const val HANDOVER_SEND_MS = 1_500L
        /** How long the party waits for everyone to find its new host. */
        const val HANDOVER_WAIT_MS = 30_000L
        /** How much better (out of 100) another phone must be to take hosting over in the lobby. */
        const val BETTER_HOST_MARGIN = 20
        /** A guest's host score goes to the host this often. */
        const val FITNESS_EVERY_MS = 60_000L
        /** The guests' copies go out at most this often. */
        const val SHADOW_GAP_MS = 1_000L
        /** The radio refused to make the phone findable: tried again after this. */
        const val FINDABLE_RETRY_MS = 5_000L
    }
}
