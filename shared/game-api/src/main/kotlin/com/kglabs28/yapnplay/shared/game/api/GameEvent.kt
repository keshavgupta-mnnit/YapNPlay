package com.kglabs28.yapnplay.shared.game.api

/**
 * A game-specific event produced by an action or autoplay.
 * Each game module defines its own event types as @Serializable data classes.
 * The server logs these and broadcasts them to clients.
 */
interface GameEvent
