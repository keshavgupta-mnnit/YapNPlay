package com.kglabs28.yapnplay.appengine.core.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** The Play Games name of the player on this phone, once known (null: not signed in to Play Games). */
class PlayGamesPlayer {
    private val name = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = name.asStateFlow()

    fun remember(displayName: String?) {
        name.value = displayName
    }
}
