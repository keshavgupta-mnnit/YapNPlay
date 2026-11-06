package com.kglabs28.yapnplay.appengine.ui.screens.signin

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState

/** The web's login screen: the Google button, busy while signing in, and why the last try failed. */
data class SignInState(
    val busy: Boolean = false,
    val error: String? = null,
) : UiState

sealed interface SignInIntent : UiIntent {
    /** The screen opened: finish a sign-in Google just came back from, if any. */
    data object Opened : SignInIntent

    /** "Continue with Google". */
    data object Google : SignInIntent
}

/** None: once signed in, the gate swaps the screen for the app by itself. */
sealed interface SignInEffect : UiEffect
