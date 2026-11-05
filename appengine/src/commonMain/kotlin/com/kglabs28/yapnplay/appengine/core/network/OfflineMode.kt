package com.kglabs28.yapnplay.appengine.core.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Whether the app plays offline right now (Nearby play): while on, every
 * match message goes to the phone hosting the party instead of the backend.
 */
class OfflineMode {
    private val _active = MutableStateFlow(false)
    val active: StateFlow<Boolean> = _active.asStateFlow()

    fun set(active: Boolean) {
        _active.value = active
    }
}
