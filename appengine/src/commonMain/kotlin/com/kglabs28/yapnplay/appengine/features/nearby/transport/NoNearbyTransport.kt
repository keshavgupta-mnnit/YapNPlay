package com.kglabs28.yapnplay.appengine.features.nearby.transport

import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyRadio
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/** Desktop and previews: no radios, so Nearby play is not offered. */
class NoNearbyTransport : NearbyTransport {
    override val events: Flow<TransportEvent> = emptyFlow()
    override val supported: Boolean = false
    override fun radios(): Map<NearbyRadio, Boolean> = emptyMap()
    override val lastFailure: String? = null
    override fun openRadioSettings(radio: NearbyRadio) = Unit
    override suspend fun advertise(name: String): Boolean = false
    override fun stopAdvertising() = Unit
    override suspend fun discover(): Boolean = false
    override fun stopDiscovery() = Unit
    override suspend fun connect(endpointId: String, name: String): Boolean = false
    override fun send(endpointId: String, bytes: ByteArray) = Unit
    override fun disconnect(endpointId: String) = Unit
    override fun stopAll() = Unit
}
