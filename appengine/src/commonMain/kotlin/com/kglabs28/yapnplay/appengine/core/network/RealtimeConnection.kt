package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.protocol.Rejected
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel as Outbox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.random.Random

/**
 * The app's one WebSocket to the backend (contract "WebSocket /ws"). It
 * connects on first use, reconnects with growing pauses when the network
 * drops, and offers two things:
 * - [request]: send a message and wait for its one reply (Ack, a typed
 *   result, or Rejected → [RealtimeRejectedException]);
 * - [stream]: every message on a channel, including server pushes.
 * [connections] counts successful connects, so screens can re-enter their
 * room or match after a reconnect (presence is lost when the socket drops).
 */
class RealtimeConnection(
    private val http: HttpClient,
    private val session: BackendSession,
    private val json: Json,
    private val scope: CoroutineScope,
) {
    private val incoming = MutableSharedFlow<Envelope>(extraBufferCapacity = INCOMING_BUFFER)
    private val outbox = Outbox<String>(Outbox.UNLIMITED)
    private val pending = mutableMapOf<String, CompletableDeferred<Envelope>>()
    private val pendingLock = Mutex()
    private val _connections = MutableStateFlow(0)
    private val _connected = MutableStateFlow(false)
    private val _status = MutableStateFlow(ConnectionStatus.CONNECTING)
    /** A Retry tap: cuts the pause before the next try short. */
    private val retry = Outbox<Unit>(Outbox.CONFLATED)
    /** The connect loop, started once on first use. */
    private val loop: Job by lazy { scope.launch { connectForever() } }

    /** Goes up by one every time the socket (re)connects. */
    val connections: StateFlow<Int> = _connections.asStateFlow()
    val connected: StateFlow<Boolean> = _connected.asStateFlow()
    /** Reachability for the whole app (the connecting / can't connect screens). */
    val status: StateFlow<ConnectionStatus> = _status.asStateFlow()

    /** Starts connecting now (the app does this on launch). */
    fun connect() = start()

    /** Tries again at once instead of waiting out the pause. */
    fun retryNow() {
        start()
        retry.trySend(Unit)
    }

    fun stream(channel: Channel): Flow<Envelope> {
        start()
        return incoming.filter { it.channel == channel }
    }

    /** Sends [value] as [type] on [channel] and waits for its reply. */
    suspend fun <T> request(channel: Channel, type: String, serializer: KSerializer<T>, value: T): Envelope {
        start()
        val requestId = newRequestId()
        val reply = CompletableDeferred<Envelope>()
        pendingLock.withLock { pending[requestId] = reply }
        try {
            _connected.first { it }
            val envelope = Envelope(requestId, channel, type, json.encodeToJsonElement(serializer, value).jsonObject)
            outbox.send(json.encodeToString(Envelope.serializer(), envelope))
            val answer = withTimeout(REPLY_TIMEOUT_MS) { reply.await() }
            if (answer.type == MessageType.REJECTED) {
                val rejected = json.decodeFromJsonElement(Rejected.serializer(), answer.payload)
                throw RealtimeRejectedException(rejected.code, rejected.message)
            }
            return answer
        } finally {
            pendingLock.withLock { pending.remove(requestId) }
        }
    }

    fun <T> decode(envelope: Envelope, serializer: KSerializer<T>): T = json.decodeFromJsonElement(serializer, envelope.payload)

    private fun start() {
        loop.start()
    }

    private suspend fun connectForever() {
        var attempt = 0
        while (scope.isActive) {
            try {
                val token = session.credentials().bearerToken
                http.webSocket(urlString = BackendPaths.webSocketUrl(), request = { header(HttpHeaders.Authorization, BEARER + token) }) {
                    attempt = 0
                    _connected.value = true
                    _status.value = ConnectionStatus.ONLINE
                    _connections.value += 1
                    val writer = launch { for (text in outbox) send(Frame.Text(text)) }
                    try {
                        for (frame in incoming) {
                            if (frame is Frame.Text) receive(frame.readText())
                        }
                    } finally {
                        writer.cancel()
                    }
                }
            } catch (e: Exception) {
                CrashReporter.record(e, "RealtimeConnection.connect")
            }
            _connected.value = false
            _status.value = ConnectionStatus.OFFLINE
            withTimeoutOrNull(backoff(attempt++)) { retry.receive() }
            _status.value = ConnectionStatus.CONNECTING
        }
    }

    private suspend fun receive(text: String) {
        val envelope = runCatching { json.decodeFromString(Envelope.serializer(), text) }.getOrNull() ?: return
        val waiter = pendingLock.withLock { pending[envelope.requestId] }
        if (waiter != null) waiter.complete(envelope) else incoming.emit(envelope)
        // Typed replies (snapshots) are also state everyone may want.
        if (waiter != null && envelope.type != MessageType.ACK && envelope.type != MessageType.REJECTED) incoming.emit(envelope)
    }

    private fun backoff(attempt: Int): Long =
        (BACKOFF_START_MS shl attempt.coerceAtMost(BACKOFF_MAX_SHIFT)).coerceAtMost(BACKOFF_MAX_MS)

    private fun newRequestId(): String = buildString {
        repeat(REQUEST_ID_LENGTH) { append(ID_ALPHABET[Random.nextInt(ID_ALPHABET.length)]) }
    }

    private companion object {
        const val BEARER = "Bearer "
        const val INCOMING_BUFFER = 256
        const val REPLY_TIMEOUT_MS = 15_000L
        const val BACKOFF_START_MS = 1_000L
        const val BACKOFF_MAX_MS = 30_000L
        const val BACKOFF_MAX_SHIFT = 5
        const val REQUEST_ID_LENGTH = 24
        const val ID_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"
    }
}
