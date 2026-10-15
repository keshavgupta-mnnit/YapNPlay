package com.kglabs28.yapnplay.appengine.games.base.identity

import kotlinx.serialization.Serializable

/**
 * A permanent, system-generated user identifier.
 * Unique per guest account; login providers attach to this later.
 */
@Serializable
@JvmInline
value class UserId(val value: String) {
    init {
        require(value.isNotBlank()) { "UserId must not be blank" }
    }
}
