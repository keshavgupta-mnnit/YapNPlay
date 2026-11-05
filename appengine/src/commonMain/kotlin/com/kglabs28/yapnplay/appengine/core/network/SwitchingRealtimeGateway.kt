package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.KSerializer

/**
 * The realtime link every screen uses: the backend's WebSocket ([online]),
 * or during Nearby play the link to the hosting phone ([offline]). Screens
 * never know which; a table works the same either way. [status] always
 * reports the backend's reachability (the app shows what needs internet).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SwitchingRealtimeGateway(
    private val online: RealtimeGateway,
    private val offline: RealtimeGateway,
    private val mode: OfflineMode,
    scope: CoroutineScope,
) : RealtimeGateway {
    private val current: RealtimeGateway get() = if (mode.active.value) offline else online

    override val connections: StateFlow<Int> = mode.active
        .flatMapLatest { if (it) offline.connections else online.connections }
        .stateIn(scope, SharingStarted.Eagerly, online.connections.value)

    override val connected: StateFlow<Boolean> = mode.active
        .flatMapLatest { if (it) offline.connected else online.connected }
        .stateIn(scope, SharingStarted.Eagerly, online.connected.value)

    override val status: StateFlow<ConnectionStatus> = online.status

    override fun connect() = online.connect()

    override fun retryNow() = online.retryNow()

    override fun stream(channel: Channel): Flow<Envelope> =
        mode.active.flatMapLatest { if (it) offline.stream(channel) else online.stream(channel) }

    override suspend fun <T> request(channel: Channel, type: String, serializer: KSerializer<T>, value: T): Envelope =
        current.request(channel, type, serializer, value)

    override fun <T> decode(envelope: Envelope, serializer: KSerializer<T>): T = current.decode(envelope, serializer)
}
