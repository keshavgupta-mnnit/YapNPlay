package com.kglabs28.yapnplay.appengine.features.connection.usecase

import com.kglabs28.yapnplay.appengine.core.network.PingProbe
import com.kglabs28.yapnplay.appengine.features.connection.domain.ConnectionQuality
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** The connection to the backend, measured every few seconds while collected. */
class ObserveConnectionQualityUseCase(private val probe: PingProbe) {
    operator fun invoke(): Flow<ConnectionQuality> = probe.pings().map { ConnectionQuality.of(it) }
}
