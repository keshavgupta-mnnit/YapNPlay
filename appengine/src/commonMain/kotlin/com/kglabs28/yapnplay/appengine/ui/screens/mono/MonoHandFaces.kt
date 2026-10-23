package com.kglabs28.yapnplay.appengine.ui.screens.mono

/**
 * Which card faces came into or left the viewer's hand between two
 * updates, counted one for one (a hand may hold the same face twice).
 */
object MonoHandFaces {
    /** Faces in [after] that were not in [before]. */
    fun <F> added(before: List<F>, after: List<F>): List<F> = minus(after, before)

    /** Faces in [before] that are gone from [after], leaving out [played] (the card that caused it). */
    fun <F> removed(before: List<F>, after: List<F>, played: F?): List<F> {
        val gone = minus(before, after).toMutableList()
        played?.let { gone.remove(it) }
        return gone
    }

    private fun <F> minus(from: List<F>, take: List<F>): List<F> {
        val left = take.toMutableList()
        return from.filter { face -> !left.remove(face) }
    }
}
