package com.kglabs28.yapnplay.shared.game.api

/**
 * A permanent, system-generated user identifier.
 * Unique per guest account; login providers attach to this later.
 */
@JvmInline
value class UserId(val value: String) {
    init {
        require(value.isNotBlank()) { "UserId must not be blank" }
    }
}

/**
 * A seat number within a match. Starts at 1.
 */
@JvmInline
value class SeatNumber(val value: Int) {
    init {
        require(value >= 1) { "SeatNumber must be >= 1, was $value" }
    }
}

/**
 * A piece identifier within a game. Game-specific.
 */
@JvmInline
value class PieceId(val value: String) {
    init {
        require(value.isNotBlank()) { "PieceId must not be blank" }
    }
}

/**
 * A game engine identifier (e.g., "ludo").
 */
@JvmInline
value class GameId(val value: String) {
    init {
        require(value.isNotBlank()) { "GameId must not be blank" }
    }
}
