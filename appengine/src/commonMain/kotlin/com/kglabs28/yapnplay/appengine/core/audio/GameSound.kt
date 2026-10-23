package com.kglabs28.yapnplay.appengine.core.audio

/** Short sound effects shared by the games. */
enum class GameSound {
    DICE_ROLL, HOP, CAPTURE, WIN, YOUR_TURN, TICK, HURRY_TICK,
    /** A card lands on the discard pile. */
    CARD_PLAY,
    /** A card slides off the draw pile. */
    CARD_DRAW,
    /** The discard pile is shuffled into a new draw pile. */
    CARD_SHUFFLE,
    /** Someone calls MONO. */
    MONO_CALL,
    /** CAUGHT: someone pays a penalty for a missed (or wrong) call. */
    CAUGHT,
}
