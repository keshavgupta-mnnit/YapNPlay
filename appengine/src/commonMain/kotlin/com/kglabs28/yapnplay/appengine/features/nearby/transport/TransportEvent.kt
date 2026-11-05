package com.kglabs28.yapnplay.appengine.features.nearby.transport

/** What the radio link reports. Small hierarchy, kept together. */
sealed interface TransportEvent {
    /** A findable phone around, with the name it advertises. */
    data class PeerFound(val endpointId: String, val name: String) : TransportEvent
    data class PeerLost(val endpointId: String) : TransportEvent
    data class Connected(val endpointId: String) : TransportEvent
    /** A link could not be made (refused, out of range). */
    data class ConnectFailed(val endpointId: String) : TransportEvent
    data class Disconnected(val endpointId: String) : TransportEvent
    data class Received(val endpointId: String, val bytes: ByteArray) : TransportEvent

    /** Bluetooth, Wi-Fi or Location was switched on or off on this phone. */
    data object RadiosChanged : TransportEvent
}
