package com.kglabs28.yapnplay.appengine.features.voice.domain

import com.kglabs28.yapnplay.appengine.core.network.ConnectionStatus
import com.kglabs28.yapnplay.appengine.core.network.OfflineMode
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * The app's one answer to "can anyone talk here?". Voice needs the
 * internet: with none (anywhere, a Nearby party included) it is off, every
 * mic and speaker switch shows greyed out, and no voice pass is asked for
 * and no voice room joined (so nothing fails). Online it is [available].
 */
class VoiceAvailability(offline: OfflineMode, realtime: RealtimeGateway, scope: CoroutineScope) {
    /** In a Nearby party (its mic and speaker start off). */
    val nearby: StateFlow<Boolean> = offline.active

    val available: StateFlow<Boolean> =
        realtime.status.map { it != ConnectionStatus.OFFLINE }
            .stateIn(scope, SharingStarted.Eagerly, true)
}
