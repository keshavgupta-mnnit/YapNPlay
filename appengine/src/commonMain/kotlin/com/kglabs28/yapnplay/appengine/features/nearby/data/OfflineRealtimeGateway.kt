package com.kglabs28.yapnplay.appengine.features.nearby.data

import com.kglabs28.yapnplay.appengine.core.network.ConnectionStatus
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import com.kglabs28.yapnplay.appengine.core.network.RealtimeRejectedException
import com.kglabs28.yapnplay.appengine.protocol.Rejected
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.random.Random

/**
 * The realtime link during Nearby play: requests go to the phone hosting
 * the party ([sender]: this phone's own host, or the radio to the host),
 * and its replies and pushes come back through [receive]. Behaves exactly
 * like the WebSocket ([request] waits for the one reply; Rejected throws).
 */
class OfflineRealtimeGateway(private val json: Json) : RealtimeGateway {
    private val incoming = MutableSharedFlow<Envelope>(extraBufferCapacity = INCOMING_BUFFER)
    private val pending = mutableMapOf<String, CompletableDeferred<Envelope>>()
    private val pendingLock = Mutex()
    private val _connections = MutableStateFlow(0)
    private val _connected = MutableStateFlow(false)

    /** Carries a request to the host; set while in a party. */
    var sender: (suspend (Envelope) -> Unit)? = null

    override val connections: StateFlow<Int> = _connections.asStateFlow()
    override val connected: StateFlow<Boolean> = _connected.asStateFlow()
    override val status: StateFlow<ConnectionStatus> = MutableStateFlow(ConnectionStatus.ONLINE).asStateFlow()

    /** In a party now: open tables ask for their snapshot again. */
    fun linkUp() {
        _connected.value = true
        _connections.value += 1
    }

    fun linkDown() {
        _connected.value = false
    }

    override fun connect() = Unit

    override fun retryNow() = Unit

    override fun stream(channel: Channel): Flow<Envelope> = incoming.filter { it.channel == channel }

    override suspend fun <T> request(channel: Channel, type: String, serializer: KSerializer<T>, value: T): Envelope {
        val requestId = newRequestId()
        val reply = CompletableDeferred<Envelope>()
        pendingLock.withLock { pending[requestId] = reply }
        try {
            val answer = withTimeout(REPLY_TIMEOUT_MS) {
                _connected.first { it }
                val send = sender ?: error(NOT_IN_PARTY)
                send(Envelope(requestId, channel, type, json.encodeToJsonElement(serializer, value).jsonObject))
                reply.await()
            }
            if (answer.type == MessageType.REJECTED) {
                val rejected = json.decodeFromJsonElement(Rejected.serializer(), answer.payload)
                throw RealtimeRejectedException(rejected.code, rejected.message)
            }
            return answer
        } finally {
            pendingLock.withLock { pending.remove(requestId) }
        }
    }

    override fun <T> decode(envelope: Envelope, serializer: KSerializer<T>): T = json.decodeFromJsonElement(serializer, envelope.payload)

    /** A reply or push from the host. */
    suspend fun receive(envelope: Envelope) {
        val waiter = pendingLock.withLock { pending[envelope.requestId] }
        if (waiter != null) waiter.complete(envelope) else incoming.emit(envelope)
        // Typed replies (snapshots) are also state everyone may want.
        if (waiter != null && envelope.type != MessageType.ACK && envelope.type != MessageType.REJECTED) incoming.emit(envelope)
    }

    private fun newRequestId(): String = buildString {
        repeat(REQUEST_ID_LENGTH) { append(ID_ALPHABET[Random.nextInt(ID_ALPHABET.length)]) }
    }

    private companion object {
        const val INCOMING_BUFFER = 256
        const val REPLY_TIMEOUT_MS = 15_000L
        const val REQUEST_ID_LENGTH = 24
        const val ID_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"
        const val NOT_IN_PARTY = "Not in a Nearby party"
    }
}
