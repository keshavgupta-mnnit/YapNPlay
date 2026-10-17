package com.kglabs28.yapnplay.appengine.features.voice.domain

import kotlinx.coroutines.flow.StateFlow

/** Voice controls that only affect this device (who the user hears). */
interface VoiceRepository {
    val locallyMuted: StateFlow<Set<String>>
    fun setLocallyMuted(userId: String, muted: Boolean)
}
