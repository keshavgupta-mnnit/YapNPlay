package com.kglabs28.yapnplay.appengine.features.nearby.transport

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyRadio
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
class GoogleNearbyTransport(private val context: Context) : NearbyTransport {
    private val client: ConnectionsClient = Nearby.getConnectionsClient(context)
    private val serviceId = context.packageName + SERVICE_SUFFIX
    private val _events = MutableSharedFlow<TransportEvent>(extraBufferCapacity = EVENT_BUFFER)
    private val reassemblers = mutableMapOf<String, PayloadChunks.Reassembler>()
    private var nextMessageId = 0

    override val events: Flow<TransportEvent> = _events.asSharedFlow()
    override val supported: Boolean = true

    override fun radios(): Map<NearbyRadio, Boolean> = buildMap {
        put(NearbyRadio.BLUETOOTH, context.getSystemService(BluetoothManager::class.java)?.adapter?.isEnabled == true)
        put(NearbyRadio.WIFI, context.getSystemService(WifiManager::class.java)?.isWifiEnabled == true)
        // Finding phones needs Location switched on too (many phones check it on every version).
        val location = context.getSystemService(LocationManager::class.java)
        put(NearbyRadio.LOCATION, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) location?.isLocationEnabled == true else true)
    }

    override fun openRadioSettings(radio: NearbyRadio) {
        val action = when (radio) {
            NearbyRadio.BLUETOOTH -> Settings.ACTION_BLUETOOTH_SETTINGS
            NearbyRadio.WIFI -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Settings.Panel.ACTION_WIFI else Settings.ACTION_WIFI_SETTINGS
            NearbyRadio.LOCATION -> Settings.ACTION_LOCATION_SOURCE_SETTINGS
        }
        runCatching { context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
            .onFailure { CrashReporter.record(it, "GoogleNearbyTransport.openRadioSettings") }
    }

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

    /** Bluetooth, Wi-Fi or Location switched on or off: the app checks whether it may stay findable. */
    private val radioWatch = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            _events.tryEmit(TransportEvent.RadiosChanged)
        }
    }

    init {
        val switches = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        }
        ContextCompat.registerReceiver(context, radioWatch, switches, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override suspend fun advertise(name: String): Boolean = attempt("advertise") {
        client.startAdvertising(name, serviceId, connections, AdvertisingOptions.Builder().setStrategy(STRATEGY).setDisruptiveUpgrade(false).build()).await()
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

    override var lastFailure: String? = null
        private set

    /** Runs a radio step; already running counts as started. A refusal keeps Nearby's own reason in [lastFailure]. */
    private suspend fun attempt(step: String, block: suspend () -> Unit): Boolean = try {
        block()
        lastFailure = null
        true
    } catch (e: ApiException) {
        val code = e.statusCode
        if (code == ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING || code == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
            true
        } else {
            lastFailure = ConnectionsStatusCodes.getStatusCodeString(code) + " (" + code + ")"
            CrashReporter.record(e, "GoogleNearbyTransport.$step: $lastFailure")
            false
        }
    } catch (e: Exception) {
        lastFailure = e.message ?: e::class.simpleName
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
