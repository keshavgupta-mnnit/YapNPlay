package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonObject
import kotlin.time.TimeSource

/**
 * Times a round trip to the backend every few seconds while collected:
 * the milliseconds, or null when it can't be reached (offline, or no
 * answer in time). Tells a slow network from a slow app.
 */
class PingProbe(private val realtime: RealtimeGateway) {

    fun pings(): Flow<Long?> = flow {
        while (true) {
            emit(measure())
            delay(EVERY_MS)
        }
    }

    private suspend fun measure(): Long? {
        if (realtime.status.value != ConnectionStatus.ONLINE) return null
        val start = TimeSource.Monotonic.markNow()
        return try {
            // No answer in time: unreachable (a timeout must not end the probe, so no withTimeout here).
            withTimeoutOrNull(TIMEOUT_MS) { realtime.request(Channel.MATCH, MessageType.PING, JsonObject.serializer(), JsonObject(emptyMap())) }
                ?: return null
            start.elapsedNow().inWholeMilliseconds
        } catch (e: RealtimeRejectedException) {
            // Any answer is a round trip (an older server turns the ping down, but it still answered).
            start.elapsedNow().inWholeMilliseconds
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    private companion object {
        const val EVERY_MS = 5_000L
        const val TIMEOUT_MS = 4_000L
    }
}
