package com.kglabs28.yapnplay.appengine.core.auth

import androidx.compose.runtime.Composable

/** Google Play Games on this device (Android only; elsewhere it's never signed in). */
interface PlayGamesAuth {
    /** A one-time code for the backend when the player is signed in to Play Games; null otherwise. */
    suspend fun serverAuthCode(): String?

    /** Starts the app afresh (after switching to the account Play Games brought back). */
    fun reloadApp()
}

@Composable
expect fun rememberPlayGamesAuth(): PlayGamesAuth
