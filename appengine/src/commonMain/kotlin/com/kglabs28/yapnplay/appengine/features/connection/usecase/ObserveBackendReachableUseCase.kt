package com.kglabs28.yapnplay.appengine.features.connection.usecase

import com.kglabs28.yapnplay.appengine.core.network.ConnectionStatus
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** False while the backend can't be reached (no internet, or the server is down): only Nearby play works then. */
class ObserveBackendReachableUseCase(private val realtime: RealtimeGateway) {
    operator fun invoke(): Flow<Boolean> = realtime.status.map { it != ConnectionStatus.OFFLINE }.distinctUntilChanged()
}
