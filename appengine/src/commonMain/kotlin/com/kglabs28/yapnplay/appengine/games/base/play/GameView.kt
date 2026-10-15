package com.kglabs28.yapnplay.appengine.games.base.play

/**
 * What a specific viewer (player or spectator) sees of the game state.
 * Each game defines its own view type as a @Serializable class.
 * The server serializes it to JSON and sends it in MatchSnapshot.
 */
interface GameView
