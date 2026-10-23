package com.kglabs28.yapnplay.appengine.core.common

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** The keyboard choice in the app's private preferences (survives restarts). */
class AndroidKeyboardPreference(context: Context) : KeyboardPreference {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val state = MutableStateFlow(prefs.getBoolean(KEY_SYSTEM, false))
    override val useSystem: StateFlow<Boolean> = state.asStateFlow()

    override fun setUseSystem(on: Boolean) {
        prefs.edit().putBoolean(KEY_SYSTEM, on).apply()
        state.value = on
    }

    private companion object {
        const val PREFS_NAME = "yapnplay_input"
        const val KEY_SYSTEM = "system_keyboard"
    }
}
