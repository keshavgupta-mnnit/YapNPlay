package com.kglabs28.yapnplay.appengine.core.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Which keyboard text fields open: the app's own (default) or the phone's ([useSystem]), from Settings. */
interface KeyboardPreference {
    val useSystem: StateFlow<Boolean>
    fun setUseSystem(on: Boolean)
}

/** Desktop / previews: the choice lasts while the process runs. */
class InMemoryKeyboardPreference : KeyboardPreference {
    private val state = MutableStateFlow(false)
    override val useSystem: StateFlow<Boolean> = state.asStateFlow()
    override fun setUseSystem(on: Boolean) {
        state.value = on
    }
}
