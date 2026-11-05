package com.kglabs28.yapnplay.appengine.features.nearby.transport

import kotlinx.coroutines.flow.Flow

/**
 * The radio link between phones (Android: Google Nearby Connections over
 * Bluetooth and Wi-Fi, no internet). Findable phones [advertise]; a phone
 * hosting a party [discover]s them and [connect]s to the ones it invites.
 * Both sides accept every connection; messages are byte arrays of any size.
 */
interface NearbyTransport {
    val events: Flow<TransportEvent>

    /** True when this platform can do Nearby play at all. */
    val supported: Boolean

    /** Lets phones around find this one, introduced as [name]. */
    suspend fun advertise(name: String): Boolean

    fun stopAdvertising()

    /** Looks for findable phones around. */
    suspend fun discover(): Boolean

    fun stopDiscovery()

    /** Opens a link to the phone [endpointId] (introduced as [name]). */
    suspend fun connect(endpointId: String, name: String): Boolean

    fun send(endpointId: String, bytes: ByteArray)

    fun disconnect(endpointId: String)

    /** Stops everything: advertising, discovery and every link. */
    fun stopAll()
}
