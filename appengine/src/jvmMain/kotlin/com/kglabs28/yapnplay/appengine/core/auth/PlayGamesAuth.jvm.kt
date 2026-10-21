package com.kglabs28.yapnplay.appengine.core.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberPlayGamesAuth(): PlayGamesAuth = remember {
    object : PlayGamesAuth {
        override suspend fun serverAuthCode(): String? = null
        override fun reloadApp() = Unit
    }
}
