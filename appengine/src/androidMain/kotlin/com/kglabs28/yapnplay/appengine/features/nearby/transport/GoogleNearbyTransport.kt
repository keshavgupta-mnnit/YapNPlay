package com.kglabs28.yapnplay.appengine.features.nearby.transport

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.tasks.await

/**
 * Nearby play over Google Nearby Connections: Bluetooth and Wi-Fi between
 * phones, no internet. Findable phones advertise; the host discovers them
 * and links to each one it invites (a cluster: one host, many guests).
 * Both sides accept every link at once (the invite itself asks the person).
 * Big messages travel in chunks.
 */
class GoogleNearbyTransport(context: Context) : NearbyTransport {
    private val client: ConnectionsClient = Nearby.getConnectionsClient(context)
    private val serviceId = context.packageName + SERVICE_SUFFIX
    private val _events = MutableSharedFlow<TransportEvent>(extraBufferCapacity = EVENT_BUFFER)
    private val reassemblers = mutableMapOf<String, PayloadChunks.Reassembler>()
    private var nextMessageId = 0

    override val events: Flow<TransportEvent> = _events.asSharedFlow()
    override val supported: Boolean = true

    private val payloads = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val chunk = payload.asBytes() ?: return
            val whole = reassemblers.getOrPut(endpointId) { PayloadChunks.Reassembler() }.add(chunk) ?: return
            _events.tryEmit(TransportEvent.Received(endpointId, whole))
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) = Unit
    }

    private val connections = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            client.acceptConnection(endpointId, payloads)
        }

        override fun onConnectionResult(endpointId: String, resolution: ConnectionResolution) {
            _events.tryEmit(
                if (resolution.status.isSuccess) TransportEvent.Connected(endpointId) else TransportEvent.ConnectFailed(endpointId),
            )
        }

        override fun onDisconnected(endpointId: String) {
            reassemblers.remove(endpointId)
            _events.tryEmit(TransportEvent.Disconnected(endpointId))
        }
    }

    private val discovery = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            if (info.serviceId == serviceId) _events.tryEmit(TransportEvent.PeerFound(endpointId, info.endpointName))
        }

        override fun onEndpointLost(endpointId: String) {
            _events.tryEmit(TransportEvent.PeerLost(endpointId))
        }
    }

    override suspend fun advertise(name: String): Boolean = attempt("advertise") {
        client.startAdvertising(name, serviceId, connections, AdvertisingOptions.Builder().setStrategy(STRATEGY).build()).await()
    }

    override fun stopAdvertising() = client.stopAdvertising()

    override suspend fun discover(): Boolean = attempt("discover") {
        client.startDiscovery(serviceId, discovery, DiscoveryOptions.Builder().setStrategy(STRATEGY).build()).await()
    }

    override fun stopDiscovery() = client.stopDiscovery()

    override suspend fun connect(endpointId: String, name: String): Boolean = attempt("connect") {
        client.requestConnection(name, endpointId, connections).await()
    }

    override fun send(endpointId: String, bytes: ByteArray) {
        val id = nextMessageId++
        PayloadChunks.split(id, bytes, ConnectionsClient.MAX_BYTES_DATA_SIZE).forEach { chunk ->
            client.sendPayload(endpointId, Payload.fromBytes(chunk))
                .addOnFailureListener { CrashReporter.record(it, "GoogleNearbyTransport.send") }
        }
    }

    override fun disconnect(endpointId: String) {
        reassemblers.remove(endpointId)
        client.disconnectFromEndpoint(endpointId)
    }

    override fun stopAll() {
        client.stopAdvertising()
        client.stopDiscovery()
        client.stopAllEndpoints()
        reassemblers.clear()
    }

    private suspend fun attempt(step: String, block: suspend () -> Unit): Boolean = try {
        block()
        true
    } catch (e: Exception) {
        CrashReporter.record(e, "GoogleNearbyTransport.$step")
        false
    }

    private companion object {
        /** The host links to many guests it found (it discovers, they advertise). */
        val STRATEGY: Strategy = Strategy.P2P_CLUSTER
        const val SERVICE_SUFFIX = ".nearby"
        const val EVENT_BUFFER = 256
    }
}
