package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.KSerializer

/**
 * The app's real-time link to the backend (the WebSocket,
 * [RealtimeConnection]), speaking the envelope protocol.
 */
interface RealtimeGateway {
    /** Goes up by one every time the socket (re)connects. */
    val connections: StateFlow<Int>

    val connected: StateFlow<Boolean>

    /** Reachability for the whole app (the connecting / can't connect screens). */
    val status: StateFlow<ConnectionStatus>

    /** Starts connecting now (the app does this on launch). */
    fun connect()

    /** Tries again at once instead of waiting out the pause. */
    fun retryNow()

    /** Every message on [channel], including server pushes. */
    fun stream(channel: Channel): Flow<Envelope>

    /** Sends [value] as [type] on [channel] and waits for its reply. */
    suspend fun <T> request(channel: Channel, type: String, serializer: KSerializer<T>, value: T): Envelope

    fun <T> decode(envelope: Envelope, serializer: KSerializer<T>): T
}