package com.kglabs28.yapnplay.appengine.games.base.play

/**
 * One option's expected [gain] (future earnings, progress…), [cost] paid
 * now, and [risk] (chance of a loss × its size). [note] says why, for the
 * trace. Use [Evaluation.never] for an option a person would never take.
 */
data class Evaluation(
    val gain: Double = 0.0,
    val cost: Double = 0.0,
    val risk: Double = 0.0,
    val note: String = "",
) {
    fun value(riskWeight: Double): Double = gain - cost - risk * riskWeight

    companion object {
        fun never(note: String) = Evaluation(cost = Double.POSITIVE_INFINITY, note = note)
        fun always(note: String) = Evaluation(gain = Double.POSITIVE_INFINITY, note = note)
    }
}
