package com.kglabs28.yapnplay.shared.game.api

/**
 * Random number generator interface so tests can fix the sequence.
 * Production code uses [DefaultGameRandom]; tests use a scripted implementation.
 */
interface GameRandom {
    /**
     * Returns a pseudo-random integer in [0, bound).
     * @param bound must be positive
     */
    fun nextInt(bound: Int): Int
}
