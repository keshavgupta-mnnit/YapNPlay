package com.kglabs28.yapnplay.appengine.core.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Desktop builds: no microphone; the user never shows as speaking. */
class SilentMicLevelMonitor : MicLevelMonitor {
    override val speaking: StateFlow<Boolean> = MutableStateFlow(false)
    override fun start() = Unit
    override fun stop() = Unit
}
