package com.kglabs28.yapnplay.appengine.features.nearby.data

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
import com.kglabs28.yapnplay.appengine.features.nearby.host.HostClock
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
    private val clock: HostClock,
    private val frames: NearbyFrames,
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

    init {
        scope.launch { transport.events.collect { onEvent(it) } }
    }

    /**
     * Findable only while every switch Nearby needs is already on: Nearby
     * Connections would otherwise switch Bluetooth on by itself, without the
     * user saying so.
     */
    override suspend fun becomeFindable() {
        if (!transport.supported || findable || _state.value.role != NearbyRole.IDLE) return
        if (transport.radios().values.any { !it }) return
        findable = transport.advertise(NearbyEndpointName.encode(identity.me()))
    }

    override fun stopBeingFindable() {
        if (!findable) return
        findable = false
        transport.stopAdvertising()
    }

    override fun radios(): Map<NearbyRadio, Boolean> = transport.radios()

    override fun openRadioSettings(radio: NearbyRadio) = transport.openRadioSettings(radio)

    override suspend fun host(): NearbyHostResult {
        leave()
        if (transport.radios().values.any { !it }) return NearbyHostResult.Failed(null)
        val wasFindable = findable
        stopBeingFindable()
        val host = identity.me()
        me = host
        val room = HostRoom(ROOM_ID_PREFIX + newId(), NearbyStrings.partyName(host.name), host, registry, clock)
        val hosted = HostParty(
            room = room,
            starter = starter,
            matchChannel = matchChannel,
            envelopes = envelopes,
            scope = scope,
            deliver = ::deliver,
            onGuestLeft = ::letGo,
            onHostLeft = { scope.launch { leave() } },
        )
        hosted.register(host)
        party = hosted
        // Nothing opens until the radios really look for phones: a failed start leaves no lobby behind.
        if (!transport.discover()) {
            party = null
            me = null
            transport.stopAll()
            if (wasFindable) becomeFindable()
            return NearbyHostResult.Failed(transport.lastFailure)
        }
        gateway.sender = { request -> gateway.receive(hosted.handle(host.userId, request)) }
        mode.set(true)
        gateway.linkUp()
        _state.value = NearbyState(role = NearbyRole.HOSTING, roomId = room.roomId)
        peersJob = scope.launch {
            _state.map { it.peers }.distinctUntilChanged().collect { peers -> guestLinks.values.forEach { send(it, NearbyFrame.Peers(peers)) } }
        }
        return NearbyHostResult.Hosted(room.roomId)
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
        stopBeingFindable()
        hostLink = invite.endpointId
        _state.update { it.copy(role = NearbyRole.JOINING, incoming = null, roomId = invite.roomId) }
        send(invite.endpointId, NearbyFrame.Accept(identity.me(), CONTRACT_VERSION))
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

    override suspend fun leave() {
        val hosted = party
        if (hosted != null) {
            guestLinks.values.forEach { send(it, NearbyFrame.Closed(CloseReason.ENDED)) }
            hosted.close()
        }
        val wasInParty = _state.value.role != NearbyRole.IDLE
        peersJob?.cancel()
        peersJob = null
        party = null
        me = null
        guestLinks.clear()
        hostLink = null
        if (wasInParty) {
            transport.stopAll()
            findable = false
        }
        gateway.linkDown()
        gateway.sender = null
        mode.set(false)
        _state.update { NearbyState(problem = it.problem, incoming = it.incoming) }
    }

    override fun leaveInBackground() {
        scope.launch { leave() }
    }

    // ---------------- radio events ----------------

    private suspend fun onEvent(event: TransportEvent) {
        when (event) {
            is TransportEvent.PeerFound -> NearbyEndpointName.decode(event.name)?.let { player ->
                if (party == null) return@let
                _state.update { s -> s.copy(peers = s.peers.filterNot { it.endpointId == event.endpointId || it.player.userId == player.userId } + NearbyPeer(event.endpointId, player)) }
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
            TransportEvent.RadiosChanged -> if (findable && transport.radios().values.any { !it }) stopBeingFindable()
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
            endpointId == hostLink -> stopWith(NearbyProblem.HOST_LEFT)
            _state.value.incoming?.endpointId == endpointId -> _state.update { it.copy(incoming = null) }
        }
    }

    private suspend fun onFrame(endpointId: String, frame: NearbyFrame) {
        val hosted = party
        when (frame) {
            // Someone around invites this phone (only while free).
            is NearbyFrame.Invite -> if (_state.value.role == NearbyRole.IDLE) {
                if (frame.contract != CONTRACT_VERSION) {
                    _state.update { it.copy(problem = NearbyProblem.VERSION_MISMATCH) }
                    transport.disconnect(endpointId)
                } else {
                    _state.update { it.copy(incoming = NearbyInvite(endpointId, frame.roomId, frame.roomName, frame.host)) }
                }
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
        setPeer(endpointId, PeerInvite.JOINED)
        send(endpointId, NearbyFrame.Welcome(hosted.room.roomId))
        send(endpointId, NearbyFrame.Peers(_state.value.peers))
    }

    /** Guest: the host let this phone in; its lobby opens next. */
    private fun joined() {
        val link = hostLink ?: return
        gateway.sender = { request -> send(link, NearbyFrame.Message(request)) }
        mode.set(true)
        gateway.linkUp()
        _state.update { it.copy(role = NearbyRole.GUEST) }
    }

    /** Host: a message for one person (this phone's own screens, or a guest's phone). */
    private fun deliver(userId: String, envelope: Envelope) {
        if (userId == me?.userId) {
            scope.launch { gateway.receive(envelope) }
        } else {
            guestLinks[userId]?.let { send(it, NearbyFrame.Message(envelope)) }
        }
    }

    /** Host: a guest left the lobby; their link goes. */
    private fun letGo(userId: String) {
        val link = guestLinks.remove(userId) ?: return
        _state.update { s -> s.copy(peers = s.peers.filterNot { it.endpointId == link }) }
        transport.disconnect(link)
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
    }
}
