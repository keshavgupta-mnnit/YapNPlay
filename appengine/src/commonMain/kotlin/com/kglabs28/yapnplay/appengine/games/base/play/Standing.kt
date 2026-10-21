package com.kglabs28.yapnplay.appengine.games.base.play

/**
 * Behind → take more risk, leading → less: [min] + ([max] − [min]) × how
 * close the seat is to the leader (its score / the best score, 0–1).
 */
object Standing {
    fun factor(myScore: Double, bestScore: Double, min: Double, max: Double): Double {
        val share = if (bestScore <= 0) 1.0 else (myScore / bestScore).coerceIn(0.0, 1.0)
        return min + (max - min) * share
    }
}
