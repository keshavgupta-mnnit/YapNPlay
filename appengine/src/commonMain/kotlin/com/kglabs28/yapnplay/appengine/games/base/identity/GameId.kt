package com.kglabs28.yapnplay.appengine.games.base.identity

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * A game engine identifier (e.g., "royal-ludo").
 */
@Serializable
@JvmInline
value class GameId(val value: String) {
    init {
        require(value.isNotBlank()) { "GameId must not be blank" }
    }
}
