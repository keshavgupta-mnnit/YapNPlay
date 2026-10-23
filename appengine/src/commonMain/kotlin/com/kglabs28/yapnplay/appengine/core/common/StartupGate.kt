package com.kglabs28.yapnplay.appengine.core.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The order things pop up when the app opens: Play Games sign-in first (its
 * dialog is dismissed by anything that opens over it, and the account it
 * restores reloads the app), and only after it the permission requests,
 * the daily bonus and the free-coins offer. One for the app's life.
 */
class StartupGate {
    private val signInDone = MutableStateFlow(false)

    /** True once the launch's Play Games sign-in has finished (signed in, refused or timed out). */
    val ready: StateFlow<Boolean> = signInDone.asStateFlow()

    fun open() {
        signInDone.value = true
    }
}
