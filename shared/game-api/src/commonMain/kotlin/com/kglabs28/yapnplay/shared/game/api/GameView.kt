package com.kglabs28.yapnplay.shared.game.api

/**
 * What a specific viewer (player or spectator) sees of the game state.
 * Each game module defines its own view type as a @Serializable class.
 * The server serializes it to JSON and sends it in MatchSnapshot.
 */
interface GameView
